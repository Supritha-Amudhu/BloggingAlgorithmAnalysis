package edu.sfsu.bigdata.bloganalysis.core;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PageRank {

    private String filename;
    private double approxParam;
    private final double beta = 0.85;
    private HashMap<String,Double> pageRanks;
    private HashMap<String,Integer> inDegrees;
    private HashMap<String,Integer> outDegrees;
    private HashMap<String,Set<String>> pageLinks;
    private HashMap<String,Integer> pageIndex;
    private String[] sortedPageRank;
    private String[] sortedInDegree;
    private String[] sortedOutDegree;

    private int numEdges;

    public PageRank(String filename, double approxParam){
        this.filename = filename;
        this.approxParam = approxParam;

        this.pageRanks = new HashMap<>();
        this.inDegrees = new HashMap<>();
        this.outDegrees = new HashMap<>();
        this.pageLinks = new HashMap<>();
        this.pageIndex = new HashMap<>();
        calculatePageRanks();
        createSortedPageRank();
        createSortedDegrees();
    }

    private void createSortedDegrees() {
        SortedSet<Map.Entry<String, Integer>> sortedset = new TreeSet<Map.Entry<String, Integer>>(
                new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> e1,
                                       Map.Entry<String, Integer> e2) {
                        if(e2.getValue().compareTo(e1.getValue()) == 0){
                            return e1.getKey().compareTo(e2.getKey());
                        }else
                            return e2.getValue().compareTo(e1.getValue());
                    }
                });
        sortedset.addAll(this.inDegrees.entrySet());
        Iterator<Map.Entry<String,Integer>> iterator = sortedset.iterator();
        this.sortedInDegree = new String[this.inDegrees.size()];
        int i=0;
        while(iterator.hasNext()){
            this.sortedInDegree[i++] = iterator.next().getKey();
        }
        sortedset.clear();
        sortedset.addAll(this.outDegrees.entrySet());
        iterator = sortedset.iterator();
        this.sortedOutDegree = new String[this.outDegrees.size()];
        i=0;
        while(iterator.hasNext()){
            this.sortedOutDegree[i++] = iterator.next().getKey();
        }
    }

    private void createSortedPageRank() {
        SortedSet<Map.Entry<String, Double>> sortedset = new TreeSet<Map.Entry<String, Double>>(
                new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> e1,
                                       Map.Entry<String, Double> e2) {
                        if(e2.getValue().compareTo(e1.getValue()) == 0){
                            return e1.getKey().compareTo(e2.getKey());
                        }else
                            return e2.getValue().compareTo(e1.getValue());
                    }
                });
        sortedset.addAll(this.pageRanks.entrySet());
        Iterator<Map.Entry<String,Double>> iterator = sortedset.iterator();
        this.sortedPageRank = new String[this.pageRanks.size()];
        int i=0;
        while(iterator.hasNext()){
            this.sortedPageRank[i++] = iterator.next().getKey();
        }
    }


    public int inDegreeOf(String pageName){
        return inDegrees.get(pageName);
    }

    public int outDegreeOf(String pageName){
        return outDegrees.get(pageName);
    }

    public double pageRankOf(String pageName){
        return pageRanks.get(pageName);
    }

    public int numEdges(){
        return numEdges;
    }

    public String[] topKPageRank(int k){
        int size = this.sortedPageRank.length<k?this.sortedPageRank.length:k;
        String[] topKPages = new String[size];
        for(int i=0;i<size;i++) {
        	topKPages[i] = this.sortedPageRank[i];
        }
        return topKPages;
    }

    public String[] topKInDegree(int k){
        int size = this.sortedInDegree.length<k?this.sortedInDegree.length:k;
        String[] topKInDegree = new String[size];
        for(int i=0;i<size;i++) {
            topKInDegree[i] = this.sortedInDegree[i];
        }
        return topKInDegree;
    }

    public String[] topKOutDegree(int k){
        int size = this.sortedOutDegree.length<k?this.sortedOutDegree.length:k;
        String[] topKOutDegree = new String[size];
        for(int i=0;i<size;i++) {
            topKOutDegree[i] = this.sortedOutDegree[i];
        }
        return topKOutDegree;
    }

    private void parseGraph(){
        
        try {
            FileReader fileReader = new FileReader(new File(this.filename));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            double nodes = 0;
            if(bufferedReader.ready()){
                nodes = Integer.parseInt(bufferedReader.readLine());
            }
            while(bufferedReader.ready()){
                String edge = bufferedReader.readLine();
                String[] edgeNodes = edge.split(" ");
                if(edgeNodes.length == 2){
                    this.numEdges++;

                    if(!this.inDegrees.containsKey(edgeNodes[0])){
                        this.inDegrees.put(edgeNodes[0],0);
                    }

                    if(!this.outDegrees.containsKey(edgeNodes[1])){
                        this.outDegrees.put(edgeNodes[1],0);
                    }

                    if(this.inDegrees.containsKey(edgeNodes[1])){
                        int temp = this.inDegrees.get(edgeNodes[1]);
                        this.inDegrees.put(edgeNodes[1],temp+1);
                    }else{
                        this.inDegrees.put(edgeNodes[1],1);
                    }

                    if(this.outDegrees.containsKey(edgeNodes[0])){
                        int temp = this.outDegrees.get(edgeNodes[0]);
                        this.outDegrees.put(edgeNodes[0],temp+1);
                    }else{
                        this.outDegrees.put(edgeNodes[0],1);
                    }

                    if(this.pageLinks.containsKey(edgeNodes[0])){
                        this.pageLinks.get(edgeNodes[0]).add(edgeNodes[1]);
                    }else{
                        Set<String> links = new TreeSet<>();
                        links.add(edgeNodes[1]);
                        this.pageLinks.put(edgeNodes[0],links);
                    }

                    this.pageRanks.put(edgeNodes[0],new Double(1/nodes));
                    this.pageRanks.put(edgeNodes[1],new Double(1/nodes));

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculatePageRanks() {
        parseGraph();
        createPageIndex();
        int n=1;
        double norm = 0.0;
        boolean converged = false;
        HashMap<String,Double> currentRank = this.pageRanks;
        HashMap<String,Double> nextRank;
        while(!converged){
            nextRank = computeNextP(currentRank);
            norm = normalize(nextRank,currentRank);
            System.out.println(norm);
            if(norm <= this.approxParam){
                converged = true;
                this.pageRanks = nextRank;
                break;
            }
            currentRank = nextRank;
            n++;
        }
    }

    private double getSumOfPageRanks(HashMap<String,Double> currentRank){
        double pgr = 0.0;
        for(String page : currentRank.keySet()){
            pgr += currentRank.get(page);
        }
        return pgr;
    }

    private double normalize(HashMap<String, Double> nextRank, HashMap<String, Double> currentRank) {
        Iterator<String> keysIterator = nextRank.keySet().iterator();
        double normalize = 0;
        while(keysIterator.hasNext()){
            String page = keysIterator.next();
            normalize+= Math.abs(nextRank.get(page)-currentRank.get(page));
        }
        return normalize;
    }

    private HashMap<String,Double> computeNextP(HashMap<String,Double> prevRank){
        HashMap<String,Double> nextRank = new HashMap<>();
        initializeNextP(nextRank,prevRank);
        Iterator<String> pageIterator = nextRank.keySet().iterator();
        while(pageIterator.hasNext()){
            String page = pageIterator.next();
            if(this.outDegrees.get(page) != 0){
                Set<String> linksInPage = this.pageLinks.get(page);
                Iterator<String> linksIterator = linksInPage.iterator();
                while(linksIterator.hasNext()){
                    String link = linksIterator.next();
                    double currentRank = nextRank.get(link);
                    currentRank += beta*(prevRank.get(page)/linksInPage.size());
                    nextRank.put(link,currentRank);
                }
            }else{
                Set<String> allPages = this.pageLinks.keySet();
                Iterator<String> allPagesIterator = allPages.iterator();
                while(allPagesIterator.hasNext()){
                    String nextPage = allPagesIterator.next();
                    double currentRank = nextRank.get(nextPage);
                    currentRank += beta*(prevRank.get(page)/allPages.size());
                    nextRank.put(nextPage,currentRank);
                }

            }
        }

        return nextRank;
    }

    private void initializeNextP(HashMap<String, Double> nextRank, HashMap<String, Double> prevRank) {
        Iterator<String> keySet = prevRank.keySet().iterator();
        double val = (1-beta)/prevRank.size();
        while(keySet.hasNext()){
            nextRank.put(keySet.next(),val);
        }

    }

    private void createPageIndex(){
        Set<String> pages = this.pageRanks.keySet();
        Iterator<String> pageIterator = pages.iterator();
        int index =0;
        while(pageIterator.hasNext()){
            this.pageIndex.put(pageIterator.next(),index++);
        }
    }

    public static void main(String[] args){
        PageRank pageRank = new PageRank("edges.txt",0.005);
        double pgr = 0.0;
        System.out.println("Top N page Ranks - ");
        for(String s :pageRank.topKPageRank(500)){
        	if(s.startsWith("https://medium.com/")){
        		pgr += pageRank.pageRankOf(s);
                System.out.println(s+"\t"+pageRank.pageRankOf(s));
        	}
        }
        /*System.out.println("Top 15 In Degrees - ");
        for(String s: pageRank.topKInDegree(15)){
            System.out.println(s+"\t"+pageRank.inDegreeOf(s));
        }
        System.out.println("Top 15 Out Degrees - ");
        for(String s: pageRank.topKOutDegree(15)){
            System.out.println(s+"\t"+pageRank.outDegreeOf(s));
        }*/

    }
}
