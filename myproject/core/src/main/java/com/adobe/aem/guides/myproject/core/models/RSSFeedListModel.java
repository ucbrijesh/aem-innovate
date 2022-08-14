package com.adobe.aem.guides.myproject.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bchauhanu
 * Fetch list of rss feeds dynamically
 */
@Model(adaptables = Resource.class)
public class RSSFeedListModel {

	/*XML Entries*/
	static final String TITLE = "title"; /*RSS Feed Title */
	static final String DESCRIPTION = "description"; /*RSS Feed Description */
	static final String LINK = "link"; /*RSS Feed Link */
	static final String ITEM = "item"; /*RSS Feed Single Item*/
	static final String PUBLISH_DATE = "updated"; /*RSS Feed Updated Date*/

	/*Logger*/
	private static final Logger LOG = LoggerFactory.getLogger(RSSFeedListModel.class);

	/*Final RSS Feed List */
	public List<RSSFeedModel> listOfRssFeeds = new ArrayList<>();

	/*RSS Feed URL from Resource */
	@Inject
	@Optional
	public String endpointurl;

	/* Max. number of RSS Feeds to display */
	@Inject
	@Optional
	public String numberOfFeeds;
	
	/*RSS Feed URL from Resource */
	@Inject
	@Optional
	public String listFrom;

	/* Fetch RSS Feeds based on limit provided in Component Dialog */
	@PostConstruct
	public List<RSSFeedModel> getRssFeedItems() throws IOException {
		if(StringUtils.isNotEmpty(getListFrom()) && StringUtils.equalsIgnoreCase(getListFrom(), "dynamic") ) {
			listOfRssFeeds = getAllFeeds();
			if(StringUtils.isNotEmpty(getNumberOfFeeds())) {
				List<RSSFeedModel> tempList = new ArrayList<>();
				int count = Integer.parseInt(getNumberOfFeeds());
				//Check if RSS Feeds are more than count provided in Component dialog
				if(listOfRssFeeds.size() > count) {
					for(int i=0;i<count;i++) {
						tempList.add(listOfRssFeeds.get(i));
					}
					listOfRssFeeds = tempList;
				}
			}
		}
		return listOfRssFeeds;
	}
	
	/* Get All Feeds from URL endpoint  */
	private List<RSSFeedModel> getAllFeeds() {
		try {
			String description = "";
			String title = "";
			String link = "";
			String publishDate = "";
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			if(null != getEndpointurl()) {
				URL url = new URL(getEndpointurl());
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();//Http call to endpoint url
				if(connection!=null ) {
					connection.addRequestProperty("User-Agent", "Mozilla");
					InputStream inputStream = connection.getInputStream();  
					XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
					//code for parsing xml file
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						if (event.isStartElement()) {
							String localPart = event.asStartElement().getName()
									.getLocalPart();
							switch (localPart) {
							case ITEM:
								event = eventReader.nextEvent();
								break;
							case TITLE:
								title = getCharacterData(event, eventReader);
								break;
							case DESCRIPTION:
								description = getCharacterData(event, eventReader);
								break;
							case LINK:
								link = getCharacterData(event, eventReader);
								break;
							case PUBLISH_DATE:
								publishDate = getCharacterData(event, eventReader);
								break;
							}

						}else if (event.isEndElement() && event.asEndElement().getName().getLocalPart() == (ITEM)) {
							RSSFeedModel feed = new RSSFeedModel();
							feed.setDescription(description);
							feed.setLink(link);
							feed.setTitle(title);
							feed.setPublishDate(formatDate(publishDate));
							feed.setFeedDate(getDate(publishDate));
							listOfRssFeeds.add(feed);
							LOG.info("FEED TITLE::::::::::::::::::::::" + feed.getTitle());
							event = eventReader.nextEvent();
							continue;
						}
					}
				}
				//Sorts list based on last updated date and most recent 
				if(listOfRssFeeds != null && listOfRssFeeds.size()>1) {
					Collections.sort(listOfRssFeeds, new Comparator<RSSFeedModel>() {
					    @Override
					    public int compare(RSSFeedModel r1, RSSFeedModel r2) {
					        return r2.getFeedDate().compareTo(r1.getFeedDate());
					    }
					});
				}
			} 
		} catch (Exception e) {
			LOG.error("Exception in getRssFeedItems()" +e.getMessage());
		}

		return listOfRssFeeds;
		
	}


	/* Retrieve LocalDateTIme object for sorting  */
	private LocalDateTime getDate(String publishDate) {
		LocalDateTime date = LocalDateTime.parse(publishDate, DateTimeFormatter.ISO_DATE_TIME);
		return date;
	}

	/* Get Formatted Date value from XML */
	private String formatDate(String publishDate) {
		String formattedDate = "";
		try {
			LocalDateTime date = LocalDateTime.parse(publishDate, DateTimeFormatter.ISO_DATE_TIME);
			formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm"));
		} catch (Exception e) {
			LOG.error("exception at "+e.getMessage());
		}
		return formattedDate;
	}

	/* Get individual feed details from XML - title, description, link etc */
	private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
			throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		LOG.info("Event string::"+ event.toString());
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		LOG.info("Return string::"+ result);
		return result;
	}
	
	//Model getters
	public String getEndpointurl() {
		return endpointurl;
	}

	public String getNumberOfFeeds() {
		return numberOfFeeds;
	}

	public String getListFrom() {
		return listFrom;
	}

}
