package edu.sfsu.bigdata.bloganalysis.core;

import edu.sfsu.bigdata.bloganalysis.model.Blog;
import edu.sfsu.bigdata.bloganalysis.parser.BlogParser;

public class BloggingAlgorithm {
	public static double getBlogScore(Blog blog) {
		double bloggingScore = 0;
		long bodyLength = blog.getBlogLength();
		long commentsCount = blog.getCommentsCount();
		long outlinksCount = blog.getOutlinks().size();
		long inlinksCount = blog.getInLinkCount();
		bloggingScore = bodyLength * (commentsCount + (inlinksCount - outlinksCount));
		return bloggingScore;
	}
	
	public static void main(String[] args) {
		Blog blog = BlogParser.parseBlog("https:"
				+ "//medium.freecodecamp.org/software-engineering-interviews-744380f4f2af");
		blog.setInLinkCount(5);
		System.out.println(BloggingAlgorithm.getBlogScore(blog));
	}
}
