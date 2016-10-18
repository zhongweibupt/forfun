/**
 * 
 */
package zhongwei.forfun.nbc;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
* <p>Title: Article.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月15日
* @version 1.0
*/
public class Article {
	public static enum MODE{STOP, SIMPLE, WHITESPACE, STANDARD}; 
	
	private String label;
	
	private int realLength;
	private String textOriginal;
	private List<String> wordsListAll;
	private List<String> wordsVectorSorted;
	private Map<String, Integer> termFrequency;
	
	public Article(String textOriginal) {
		this.realLength = 0;
		this.textOriginal = textOriginal;
		this.wordsListAll = new LinkedList<String>();
		this.wordsVectorSorted = new LinkedList<String>();
		this.termFrequency = new TreeMap<String, Integer>();
	}
	
	@SuppressWarnings("unchecked")
	public void wordsCount() throws IOException {
		this.wordsListAll = segment(this.textOriginal, MODE.STANDARD);
		
		this.termFrequency.clear();
		for(String word : this.wordsListAll) {
			int tmp = this.termFrequency.containsKey(word) ? this.termFrequency.get(word) : 0;
			this.termFrequency.put(word, ++tmp);
		}
		
		this.wordsVectorSorted = WordsTable.sortMap(termFrequency);
	}
	
	public void filter(List<String> stopWords) {
		this.wordsListAll.removeAll(stopWords);
		this.wordsVectorSorted.removeAll(stopWords);
		for(String word : stopWords) {
			this.termFrequency.remove(word);
		}
		
		this.realLength = this.wordsListAll.size();
	}
	
	private List<String> segment(String text, MODE mode) throws IOException {
		List<String> wordsList = new LinkedList<String>();
		
		Analyzer analyzer;
		switch(mode) {
		case STOP :
			analyzer = new StopAnalyzer();
			break;
		case SIMPLE :
			analyzer = new SimpleAnalyzer();
			break;
		case WHITESPACE:
			analyzer = new WhitespaceAnalyzer();
			break;
		case STANDARD :
			analyzer = new StandardAnalyzer();
			break;
		default :
			analyzer = new StandardAnalyzer();
			break;
		}
		
		TokenStream stream  = analyzer.tokenStream("", new StringReader(text));
		//Stemming
		stream = new PorterStemFilter(stream);
		stream.reset();
		CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        while(stream.incrementToken()){
        	String word = cta.toString();
            wordsList.add(word);
        }
        
		return wordsList;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<String> getWords() {
		return this.wordsVectorSorted;
	}
	
	public int getFrequencyOfWord(String word) {
		return this.termFrequency.get(word);
	}
	
	public String getText() {
		return this.textOriginal;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getLength() {
		return this.realLength;
	}
	
	public static void main(String args[]) throws IOException {
		String text = "Dear Paul,   This is with regards to your expense report _ VenaSeal Launch Meeting.   As per policy formal approval for intercontinental travel in economy class from direct manager is required so, so could you please provide us the approval.   Expense line item dated 20/02/2025 Airfare amounting 825.20 EUR,     Thank you in advance for your cooperation.   Priyanka T&E Team Member | T&E Department aabbcc [AG, organizacni slozka], a aabbcc Company E-mail: Teemea@aabbccaaabbb aabbccaaabbb   |  Facebook   |  LinkedIn   |  Twitter   |  YouTube   qqq   For any question related to travel expenses visit our EMEA T&E InfoPoint  and   contact Us on the T&E Helpdesk . This information may be confidential and/or privileged. Use of this information by anyone other than the intended recipient is prohibited. If you receive this in error, please inform the sender and remove any record of this message.  P Please consider the environment before printing this email.";
		Article article = new Article(text);
		article.wordsCount();
	}

}
