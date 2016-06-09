package org.apache.nutch.protocol.selenium;

// JDK imports
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.net.protocols.Response;
import org.apache.nutch.protocol.http.api.HttpBase;
import org.apache.nutch.protocol.ProtocolException;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;

import org.apache.nutch.protocol.selenium.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.nutch.protocol.selenium.RegexURLFilter;

public class Http extends HttpBase {

  public static final Logger LOG = LoggerFactory.getLogger(Http.class);

  private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

  private static RegexURLFilter regexUrlFilter;

  static {
    FIELDS.add(WebPage.Field.MODIFIED_TIME);
    FIELDS.add(WebPage.Field.HEADERS);
  }

  public Http() {
    super(LOG);
  }

  @Override
  public void setConf(Configuration conf) {
    regexUrlFilter = new RegexURLFilter();
    regexUrlFilter.setConf(conf);

    super.setConf(conf);
  }

  public static void main(String[] args) throws Exception {
    Http http = new Http();
    http.setConf(NutchConfiguration.create());
    main(http, args);
  }

    @Override
    protected Response getResponse(URL url, WebPage page, boolean redirect)
            throws ProtocolException, IOException {
        String urlFiltered = null;
        try {
          urlFiltered = regexUrlFilter.filter(url.toString()); // filter the url
        } catch (Exception e) {
          urlFiltered = null;
        }

        return new HttpResponse(this, url, page, getConf(), urlFiltered != null);
    }

  @Override
  public Collection<WebPage.Field> getFields() {
    return FIELDS;
  }
}
