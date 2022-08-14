/**
 * 
 */
package com.adobe.aem.guides.myproject.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.aem.guides.myproject.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * @author bchauhanu
 *
 */
@ExtendWith(AemContextExtension.class)
class RSSFeedListModelTest {
	
	private final AemContext aemContext = AppAemContext.newAemContext();
	
	RSSFeedListModel rSSFeedListModel;
	
	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(RSSFeedListModel.class);
		aemContext.load().json("src/test/java/resources/RSSFeedListModel.json", "/mycomponent");
	}
	
	/* Test EndPoint URL*/
	@Test
	void getEndpointurl() {
		rSSFeedListModel = aemContext.currentResource("/mycomponent/rss_feed").adaptTo(RSSFeedListModel.class);
		assertEquals("https://sports.ndtv.com/rss/cricket",rSSFeedListModel.getEndpointurl());		
	}
	
	/* Check for valid EndPoint URL*/
	@Test
	void validUrl() {
		rSSFeedListModel = aemContext.currentResource("/mycomponent/rss_feed").adaptTo(RSSFeedListModel.class);
		try {
			new URL(rSSFeedListModel.getEndpointurl()).toURI();
			assertTrue(true);
		} catch (Exception e){
			assertTrue(false);
		}
	}
	
	/* Test count of feeds*/
	@Test
	void getNumberOfFeeds() throws Exception {
		rSSFeedListModel = aemContext.currentResource("/mycomponent/rss_feed").adaptTo(RSSFeedListModel.class);
		assertEquals("4",rSSFeedListModel.getNumberOfFeeds());		
	}
	
	/* Test Dialog dropdown value*/
	@Test
	void getListFrom() throws Exception {
		rSSFeedListModel = aemContext.currentResource("/mycomponent/rss_feed").adaptTo(RSSFeedListModel.class);
		assertEquals("dynamic",rSSFeedListModel.getListFrom());		
	}
	
	/* Test if Fixed list of feeds exist*/
	@Test
	void hasFeeds() throws Exception {
		Resource feeds = aemContext.currentResource("/mycomponent/rss_feed").getChild("feeds");
		if(feeds.hasChildren())
			assertTrue(true);
		else
			assertTrue(false);
	}
}
