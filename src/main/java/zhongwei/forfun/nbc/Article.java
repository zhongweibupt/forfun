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
		
		this.wordsVectorSorted = WordsTable.sortMap(termFrequency, new Comparator<Object>() {  
            public int compare(Object o1, Object o2) {  
            	Entry<String, Integer> obj1 = (Entry<String, Integer>) o1;  
            	Entry<String, Integer> obj2 = (Entry<String, Integer>) o2;  
                return (obj2.getValue()).compareTo(obj1.getValue());  
                }  
        });
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
	
	public boolean containsWord(String word) {
		return this.wordsListAll.contains(word);
	}
	
	public int getFrequencyOfWord(String word) {
		return this.containsWord(word) ? 
				this.termFrequency.get(word) : 0;
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
}
