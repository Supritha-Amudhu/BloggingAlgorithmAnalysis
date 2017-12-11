package edu.sfsu.bigdata.bloganalysis.model;

import java.util.List;

import org.jsoup.nodes.Document;

public class Blog {
	private String blogName;
	private Link url;
	private String blogHTML;
	private List<Link> outlinks;
	private long inLinkCount;
	private Document htmlDocument;
	private long commentsCount;
	private long blogLength;
	
	public Blog(String blogURL) {
		this.url = new Link(blogURL); 
	}
	
	public String getBlogName() {
		return blogName;
	}
	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}
	public Link getUrl() {
		return url;
	}
	public void setUrl(Link url) {
		this.url = url;
	}
	public String getBlogHTML() {
		return blogHTML;
	}
	public void setBlogHTML(String blogHTML) {
		this.blogHTML = blogHTML;
	}
	public List<Link> getOutlinks() {
		return outlinks;
	}
	public void setOutlinks(List<Link> outlinks) {
		this.outlinks = outlinks;
	}
	public Document getHtmlDocument() {
		return htmlDocument;
	}
	public void setHtmlDocument(Document htmlDocument) {
		this.htmlDocument = htmlDocument;
	}
	public long getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getInLinkCount() {
		return inLinkCount;
	}

	public void setInLinkCount(long inLinkCount) {
		this.inLinkCount = inLinkCount;
	}

	public long getBlogLength() {
		return blogLength;
	}

	public void setBlogLength(long blogLength) {
		this.blogLength = blogLength;
	}
	
}
