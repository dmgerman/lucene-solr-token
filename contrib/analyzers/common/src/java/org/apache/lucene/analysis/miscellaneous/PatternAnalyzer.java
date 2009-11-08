begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Efficient Lucene analyzer/tokenizer that preferably operates on a String rather than a  * {@link java.io.Reader}, that can flexibly separate text into terms via a regular expression {@link Pattern}  * (with behaviour identical to {@link String#split(String)}),  * and that combines the functionality of  * {@link org.apache.lucene.analysis.LetterTokenizer},  * {@link org.apache.lucene.analysis.LowerCaseTokenizer},  * {@link org.apache.lucene.analysis.WhitespaceTokenizer},  * {@link org.apache.lucene.analysis.StopFilter} into a single efficient  * multi-purpose class.  *<p>  * If you are unsure how exactly a regular expression should look like, consider   * prototyping by simply trying various expressions on some test texts via  * {@link String#split(String)}. Once you are satisfied, give that regex to   * PatternAnalyzer. Also see<a target="_blank"   * href="http://java.sun.com/docs/books/tutorial/extra/regex/">Java Regular Expression Tutorial</a>.  *<p>  * This class can be considerably faster than the "normal" Lucene tokenizers.   * It can also serve as a building block in a compound Lucene  * {@link org.apache.lucene.analysis.TokenFilter} chain. For example as in this   * stemming example:  *<pre>  * PatternAnalyzer pat = ...  * TokenStream tokenStream = new SnowballFilter(  *     pat.tokenStream("content", "James is running round in the woods"),   *     "English"));  *</pre>  *  */
end_comment

begin_class
DECL|class|PatternAnalyzer
specifier|public
class|class
name|PatternAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**<code>"\\W+"</code>; Divides text at non-letters (NOT Character.isLetter(c)) */
DECL|field|NON_WORD_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|NON_WORD_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\W+"
argument_list|)
decl_stmt|;
comment|/**<code>"\\s+"</code>; Divides text at whitespaces (Character.isWhitespace(c)) */
DECL|field|WHITESPACE_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|WHITESPACE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
DECL|field|EXTENDED_ENGLISH_STOP_WORDS
specifier|private
specifier|static
specifier|final
name|CharArraySet
name|EXTENDED_ENGLISH_STOP_WORDS
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"about"
argument_list|,
literal|"above"
argument_list|,
literal|"across"
argument_list|,
literal|"adj"
argument_list|,
literal|"after"
argument_list|,
literal|"afterwards"
argument_list|,
literal|"again"
argument_list|,
literal|"against"
argument_list|,
literal|"albeit"
argument_list|,
literal|"all"
argument_list|,
literal|"almost"
argument_list|,
literal|"alone"
argument_list|,
literal|"along"
argument_list|,
literal|"already"
argument_list|,
literal|"also"
argument_list|,
literal|"although"
argument_list|,
literal|"always"
argument_list|,
literal|"among"
argument_list|,
literal|"amongst"
argument_list|,
literal|"an"
argument_list|,
literal|"and"
argument_list|,
literal|"another"
argument_list|,
literal|"any"
argument_list|,
literal|"anyhow"
argument_list|,
literal|"anyone"
argument_list|,
literal|"anything"
argument_list|,
literal|"anywhere"
argument_list|,
literal|"are"
argument_list|,
literal|"around"
argument_list|,
literal|"as"
argument_list|,
literal|"at"
argument_list|,
literal|"be"
argument_list|,
literal|"became"
argument_list|,
literal|"because"
argument_list|,
literal|"become"
argument_list|,
literal|"becomes"
argument_list|,
literal|"becoming"
argument_list|,
literal|"been"
argument_list|,
literal|"before"
argument_list|,
literal|"beforehand"
argument_list|,
literal|"behind"
argument_list|,
literal|"being"
argument_list|,
literal|"below"
argument_list|,
literal|"beside"
argument_list|,
literal|"besides"
argument_list|,
literal|"between"
argument_list|,
literal|"beyond"
argument_list|,
literal|"both"
argument_list|,
literal|"but"
argument_list|,
literal|"by"
argument_list|,
literal|"can"
argument_list|,
literal|"cannot"
argument_list|,
literal|"co"
argument_list|,
literal|"could"
argument_list|,
literal|"down"
argument_list|,
literal|"during"
argument_list|,
literal|"each"
argument_list|,
literal|"eg"
argument_list|,
literal|"either"
argument_list|,
literal|"else"
argument_list|,
literal|"elsewhere"
argument_list|,
literal|"enough"
argument_list|,
literal|"etc"
argument_list|,
literal|"even"
argument_list|,
literal|"ever"
argument_list|,
literal|"every"
argument_list|,
literal|"everyone"
argument_list|,
literal|"everything"
argument_list|,
literal|"everywhere"
argument_list|,
literal|"except"
argument_list|,
literal|"few"
argument_list|,
literal|"first"
argument_list|,
literal|"for"
argument_list|,
literal|"former"
argument_list|,
literal|"formerly"
argument_list|,
literal|"from"
argument_list|,
literal|"further"
argument_list|,
literal|"had"
argument_list|,
literal|"has"
argument_list|,
literal|"have"
argument_list|,
literal|"he"
argument_list|,
literal|"hence"
argument_list|,
literal|"her"
argument_list|,
literal|"here"
argument_list|,
literal|"hereafter"
argument_list|,
literal|"hereby"
argument_list|,
literal|"herein"
argument_list|,
literal|"hereupon"
argument_list|,
literal|"hers"
argument_list|,
literal|"herself"
argument_list|,
literal|"him"
argument_list|,
literal|"himself"
argument_list|,
literal|"his"
argument_list|,
literal|"how"
argument_list|,
literal|"however"
argument_list|,
literal|"i"
argument_list|,
literal|"ie"
argument_list|,
literal|"if"
argument_list|,
literal|"in"
argument_list|,
literal|"inc"
argument_list|,
literal|"indeed"
argument_list|,
literal|"into"
argument_list|,
literal|"is"
argument_list|,
literal|"it"
argument_list|,
literal|"its"
argument_list|,
literal|"itself"
argument_list|,
literal|"last"
argument_list|,
literal|"latter"
argument_list|,
literal|"latterly"
argument_list|,
literal|"least"
argument_list|,
literal|"less"
argument_list|,
literal|"ltd"
argument_list|,
literal|"many"
argument_list|,
literal|"may"
argument_list|,
literal|"me"
argument_list|,
literal|"meanwhile"
argument_list|,
literal|"might"
argument_list|,
literal|"more"
argument_list|,
literal|"moreover"
argument_list|,
literal|"most"
argument_list|,
literal|"mostly"
argument_list|,
literal|"much"
argument_list|,
literal|"must"
argument_list|,
literal|"my"
argument_list|,
literal|"myself"
argument_list|,
literal|"namely"
argument_list|,
literal|"neither"
argument_list|,
literal|"never"
argument_list|,
literal|"nevertheless"
argument_list|,
literal|"next"
argument_list|,
literal|"no"
argument_list|,
literal|"nobody"
argument_list|,
literal|"none"
argument_list|,
literal|"noone"
argument_list|,
literal|"nor"
argument_list|,
literal|"not"
argument_list|,
literal|"nothing"
argument_list|,
literal|"now"
argument_list|,
literal|"nowhere"
argument_list|,
literal|"of"
argument_list|,
literal|"off"
argument_list|,
literal|"often"
argument_list|,
literal|"on"
argument_list|,
literal|"once one"
argument_list|,
literal|"only"
argument_list|,
literal|"onto"
argument_list|,
literal|"or"
argument_list|,
literal|"other"
argument_list|,
literal|"others"
argument_list|,
literal|"otherwise"
argument_list|,
literal|"our"
argument_list|,
literal|"ours"
argument_list|,
literal|"ourselves"
argument_list|,
literal|"out"
argument_list|,
literal|"over"
argument_list|,
literal|"own"
argument_list|,
literal|"per"
argument_list|,
literal|"perhaps"
argument_list|,
literal|"rather"
argument_list|,
literal|"s"
argument_list|,
literal|"same"
argument_list|,
literal|"seem"
argument_list|,
literal|"seemed"
argument_list|,
literal|"seeming"
argument_list|,
literal|"seems"
argument_list|,
literal|"several"
argument_list|,
literal|"she"
argument_list|,
literal|"should"
argument_list|,
literal|"since"
argument_list|,
literal|"so"
argument_list|,
literal|"some"
argument_list|,
literal|"somehow"
argument_list|,
literal|"someone"
argument_list|,
literal|"something"
argument_list|,
literal|"sometime"
argument_list|,
literal|"sometimes"
argument_list|,
literal|"somewhere"
argument_list|,
literal|"still"
argument_list|,
literal|"such"
argument_list|,
literal|"t"
argument_list|,
literal|"than"
argument_list|,
literal|"that"
argument_list|,
literal|"the"
argument_list|,
literal|"their"
argument_list|,
literal|"them"
argument_list|,
literal|"themselves"
argument_list|,
literal|"then"
argument_list|,
literal|"thence"
argument_list|,
literal|"there"
argument_list|,
literal|"thereafter"
argument_list|,
literal|"thereby"
argument_list|,
literal|"therefor"
argument_list|,
literal|"therein"
argument_list|,
literal|"thereupon"
argument_list|,
literal|"these"
argument_list|,
literal|"they"
argument_list|,
literal|"this"
argument_list|,
literal|"those"
argument_list|,
literal|"though"
argument_list|,
literal|"through"
argument_list|,
literal|"throughout"
argument_list|,
literal|"thru"
argument_list|,
literal|"thus"
argument_list|,
literal|"to"
argument_list|,
literal|"together"
argument_list|,
literal|"too"
argument_list|,
literal|"toward"
argument_list|,
literal|"towards"
argument_list|,
literal|"under"
argument_list|,
literal|"until"
argument_list|,
literal|"up"
argument_list|,
literal|"upon"
argument_list|,
literal|"us"
argument_list|,
literal|"very"
argument_list|,
literal|"via"
argument_list|,
literal|"was"
argument_list|,
literal|"we"
argument_list|,
literal|"well"
argument_list|,
literal|"were"
argument_list|,
literal|"what"
argument_list|,
literal|"whatever"
argument_list|,
literal|"whatsoever"
argument_list|,
literal|"when"
argument_list|,
literal|"whence"
argument_list|,
literal|"whenever"
argument_list|,
literal|"whensoever"
argument_list|,
literal|"where"
argument_list|,
literal|"whereafter"
argument_list|,
literal|"whereas"
argument_list|,
literal|"whereat"
argument_list|,
literal|"whereby"
argument_list|,
literal|"wherefrom"
argument_list|,
literal|"wherein"
argument_list|,
literal|"whereinto"
argument_list|,
literal|"whereof"
argument_list|,
literal|"whereon"
argument_list|,
literal|"whereto"
argument_list|,
literal|"whereunto"
argument_list|,
literal|"whereupon"
argument_list|,
literal|"wherever"
argument_list|,
literal|"wherewith"
argument_list|,
literal|"whether"
argument_list|,
literal|"which"
argument_list|,
literal|"whichever"
argument_list|,
literal|"whichsoever"
argument_list|,
literal|"while"
argument_list|,
literal|"whilst"
argument_list|,
literal|"whither"
argument_list|,
literal|"who"
argument_list|,
literal|"whoever"
argument_list|,
literal|"whole"
argument_list|,
literal|"whom"
argument_list|,
literal|"whomever"
argument_list|,
literal|"whomsoever"
argument_list|,
literal|"whose"
argument_list|,
literal|"whosoever"
argument_list|,
literal|"why"
argument_list|,
literal|"will"
argument_list|,
literal|"with"
argument_list|,
literal|"within"
argument_list|,
literal|"without"
argument_list|,
literal|"would"
argument_list|,
literal|"xsubj"
argument_list|,
literal|"xcal"
argument_list|,
literal|"xauthor"
argument_list|,
literal|"xother "
argument_list|,
literal|"xnote"
argument_list|,
literal|"yet"
argument_list|,
literal|"you"
argument_list|,
literal|"your"
argument_list|,
literal|"yours"
argument_list|,
literal|"yourself"
argument_list|,
literal|"yourselves"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * A lower-casing word analyzer with English stop words (can be shared    * freely across threads without harm); global per class loader.    */
DECL|field|DEFAULT_ANALYZER
specifier|public
specifier|static
specifier|final
name|PatternAnalyzer
name|DEFAULT_ANALYZER
init|=
operator|new
name|PatternAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|NON_WORD_PATTERN
argument_list|,
literal|true
argument_list|,
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
argument_list|)
decl_stmt|;
comment|/**    * A lower-casing word analyzer with<b>extended</b> English stop words    * (can be shared freely across threads without harm); global per class    * loader. The stop words are borrowed from    * http://thomas.loc.gov/home/stopwords.html, see    * http://thomas.loc.gov/home/all.about.inquery.html    */
DECL|field|EXTENDED_ANALYZER
specifier|public
specifier|static
specifier|final
name|PatternAnalyzer
name|EXTENDED_ANALYZER
init|=
operator|new
name|PatternAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|NON_WORD_PATTERN
argument_list|,
literal|true
argument_list|,
name|EXTENDED_ENGLISH_STOP_WORDS
argument_list|)
decl_stmt|;
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|toLowerCase
specifier|private
specifier|final
name|boolean
name|toLowerCase
decl_stmt|;
DECL|field|stopWords
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Constructs a new instance with the given parameters.    *     * @param matchVersion If>= {@link Version#LUCENE_29}, StopFilter.enablePositionIncrement is set to true    * @param pattern    *            a regular expression delimiting tokens    * @param toLowerCase    *            if<code>true</code> returns tokens after applying    *            String.toLowerCase()    * @param stopWords    *            if non-null, ignores all tokens that are contained in the    *            given stop set (after previously having applied toLowerCase()    *            if applicable). For example, created via    *            {@link StopFilter#makeStopSet(String[])}and/or    *            {@link org.apache.lucene.analysis.WordlistLoader}as in    *<code>WordlistLoader.getWordSet(new File("samples/fulltext/stopwords.txt")</code>    *            or<a href="http://www.unine.ch/info/clef/">other stop words    *            lists</a>.    */
DECL|method|PatternAnalyzer
specifier|public
name|PatternAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|boolean
name|toLowerCase
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|)
block|{
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"pattern must not be null"
argument_list|)
throw|;
if|if
condition|(
name|eqPattern
argument_list|(
name|NON_WORD_PATTERN
argument_list|,
name|pattern
argument_list|)
condition|)
name|pattern
operator|=
name|NON_WORD_PATTERN
expr_stmt|;
elseif|else
if|if
condition|(
name|eqPattern
argument_list|(
name|WHITESPACE_PATTERN
argument_list|,
name|pattern
argument_list|)
condition|)
name|pattern
operator|=
name|WHITESPACE_PATTERN
expr_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
operator|&&
name|stopWords
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|stopWords
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|toLowerCase
operator|=
name|toLowerCase
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Creates a token stream that tokenizes the given string into token terms    * (aka words).    *     * @param fieldName    *            the name of the field to tokenize (currently ignored).    * @param text    *            the string to tokenize    * @return a new token stream    */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|text
parameter_list|)
block|{
comment|// Ideally the Analyzer superclass should have a method with the same signature,
comment|// with a default impl that simply delegates to the StringReader flavour.
if|if
condition|(
name|text
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"text must not be null"
argument_list|)
throw|;
name|TokenStream
name|stream
decl_stmt|;
if|if
condition|(
name|pattern
operator|==
name|NON_WORD_PATTERN
condition|)
block|{
comment|// fast path
name|stream
operator|=
operator|new
name|FastStringTokenizer
argument_list|(
name|text
argument_list|,
literal|true
argument_list|,
name|toLowerCase
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|==
name|WHITESPACE_PATTERN
condition|)
block|{
comment|// fast path
name|stream
operator|=
operator|new
name|FastStringTokenizer
argument_list|(
name|text
argument_list|,
literal|false
argument_list|,
name|toLowerCase
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|=
operator|new
name|PatternTokenizer
argument_list|(
name|text
argument_list|,
name|pattern
argument_list|,
name|toLowerCase
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|stream
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
return|return
name|stream
return|;
block|}
comment|/**    * Creates a token stream that tokenizes all the text in the given Reader;    * This implementation forwards to<code>tokenStream(String, String)</code> and is    * less efficient than<code>tokenStream(String, String)</code>.    *     * @param fieldName    *            the name of the field to tokenize (currently ignored).    * @param reader    *            the reader delivering the text    * @return a new token stream    */
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|FastStringReader
condition|)
block|{
comment|// fast path
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|(
operator|(
name|FastStringReader
operator|)
name|reader
operator|)
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
try|try
block|{
name|String
name|text
init|=
name|toString
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|text
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Indicates whether some other object is "equal to" this one.    *     * @param other    *            the reference object with which to compare.    * @return true if equal, false otherwise    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|this
operator|==
name|DEFAULT_ANALYZER
operator|&&
name|other
operator|==
name|EXTENDED_ANALYZER
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|==
name|DEFAULT_ANALYZER
operator|&&
name|this
operator|==
name|EXTENDED_ANALYZER
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|instanceof
name|PatternAnalyzer
condition|)
block|{
name|PatternAnalyzer
name|p2
init|=
operator|(
name|PatternAnalyzer
operator|)
name|other
decl_stmt|;
return|return
name|toLowerCase
operator|==
name|p2
operator|.
name|toLowerCase
operator|&&
name|eqPattern
argument_list|(
name|pattern
argument_list|,
name|p2
operator|.
name|pattern
argument_list|)
operator|&&
name|eq
argument_list|(
name|stopWords
argument_list|,
name|p2
operator|.
name|stopWords
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns a hash code value for the object.    *     * @return the hash code.    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|DEFAULT_ANALYZER
condition|)
return|return
operator|-
literal|1218418418
return|;
comment|// fast path
if|if
condition|(
name|this
operator|==
name|EXTENDED_ANALYZER
condition|)
return|return
literal|1303507063
return|;
comment|// fast path
name|int
name|h
init|=
literal|1
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|pattern
operator|.
name|pattern
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|pattern
operator|.
name|flags
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
operator|(
name|toLowerCase
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
operator|(
name|stopWords
operator|!=
literal|null
condition|?
name|stopWords
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|h
return|;
block|}
comment|/** equality where o1 and/or o2 can be null */
DECL|method|eq
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|==
name|o2
operator|)
operator|||
operator|(
name|o1
operator|!=
literal|null
condition|?
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
else|:
literal|false
operator|)
return|;
block|}
comment|/** assumes p1 and p2 are not null */
DECL|method|eqPattern
specifier|private
specifier|static
name|boolean
name|eqPattern
parameter_list|(
name|Pattern
name|p1
parameter_list|,
name|Pattern
name|p2
parameter_list|)
block|{
return|return
name|p1
operator|==
name|p2
operator|||
operator|(
name|p1
operator|.
name|flags
argument_list|()
operator|==
name|p2
operator|.
name|flags
argument_list|()
operator|&&
name|p1
operator|.
name|pattern
argument_list|()
operator|.
name|equals
argument_list|(
name|p2
operator|.
name|pattern
argument_list|()
argument_list|)
operator|)
return|;
block|}
comment|/**    * Reads until end-of-stream and returns all read chars, finally closes the stream.    *     * @param input the input stream    * @throws IOException if an I/O error occurs while reading the stream    */
DECL|method|toString
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|len
init|=
literal|256
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|char
index|[]
name|output
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|len
operator|+
name|n
operator|>
name|output
operator|.
name|length
condition|)
block|{
comment|// grow capacity
name|char
index|[]
name|tmp
init|=
operator|new
name|char
index|[
name|Math
operator|.
name|max
argument_list|(
name|output
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|len
operator|+
name|n
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|output
expr_stmt|;
comment|// use larger buffer for future larger bulk reads
name|output
operator|=
name|tmp
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|len
operator|+=
name|n
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
comment|/**    * The work horse; performance isn't fantastic, but it's not nearly as bad    * as one might think - kudos to the Sun regex developers.    */
DECL|class|PatternTokenizer
specifier|private
specifier|static
specifier|final
class|class
name|PatternTokenizer
extends|extends
name|TokenStream
block|{
DECL|field|str
specifier|private
specifier|final
name|String
name|str
decl_stmt|;
DECL|field|toLowerCase
specifier|private
specifier|final
name|boolean
name|toLowerCase
decl_stmt|;
DECL|field|matcher
specifier|private
name|Matcher
name|matcher
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
DECL|field|locale
specifier|private
specifier|static
specifier|final
name|Locale
name|locale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|PatternTokenizer
specifier|public
name|PatternTokenizer
parameter_list|(
name|String
name|str
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|boolean
name|toLowerCase
parameter_list|)
block|{
name|this
operator|.
name|str
operator|=
name|str
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|this
operator|.
name|toLowerCase
operator|=
name|toLowerCase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|clearAttributes
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// loop takes care of leading and trailing boundary cases
name|int
name|start
init|=
name|pos
decl_stmt|;
name|int
name|end
decl_stmt|;
name|boolean
name|isMatch
init|=
name|matcher
operator|.
name|find
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMatch
condition|)
block|{
name|end
operator|=
name|matcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|pos
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
name|matcher
operator|=
literal|null
expr_stmt|;
comment|// we're finished
block|}
if|if
condition|(
name|start
operator|!=
name|end
condition|)
block|{
comment|// non-empty match (header/trailer)
name|String
name|text
init|=
name|str
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
if|if
condition|(
name|toLowerCase
condition|)
name|text
operator|=
name|text
operator|.
name|toLowerCase
argument_list|(
name|locale
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|isMatch
condition|)
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|this
operator|.
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
comment|/**    * Special-case class for best performance in common cases; this class is    * otherwise unnecessary.    */
DECL|class|FastStringTokenizer
specifier|private
specifier|static
specifier|final
class|class
name|FastStringTokenizer
extends|extends
name|TokenStream
block|{
DECL|field|str
specifier|private
specifier|final
name|String
name|str
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|field|isLetter
specifier|private
specifier|final
name|boolean
name|isLetter
decl_stmt|;
DECL|field|toLowerCase
specifier|private
specifier|final
name|boolean
name|toLowerCase
decl_stmt|;
DECL|field|stopWords
specifier|private
specifier|final
name|Set
name|stopWords
decl_stmt|;
DECL|field|locale
specifier|private
specifier|static
specifier|final
name|Locale
name|locale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FastStringTokenizer
specifier|public
name|FastStringTokenizer
parameter_list|(
name|String
name|str
parameter_list|,
name|boolean
name|isLetter
parameter_list|,
name|boolean
name|toLowerCase
parameter_list|,
name|Set
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|str
operator|=
name|str
expr_stmt|;
name|this
operator|.
name|isLetter
operator|=
name|isLetter
expr_stmt|;
name|this
operator|.
name|toLowerCase
operator|=
name|toLowerCase
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
comment|// cache loop instance vars (performance)
name|String
name|s
init|=
name|str
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|pos
decl_stmt|;
name|boolean
name|letter
init|=
name|isLetter
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|String
name|text
decl_stmt|;
do|do
block|{
comment|// find beginning of token
name|text
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|len
operator|&&
operator|!
name|isTokenChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|letter
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|len
condition|)
block|{
comment|// found beginning; now find end of token
name|start
operator|=
name|i
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|isTokenChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|letter
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|text
operator|=
name|s
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|toLowerCase
condition|)
name|text
operator|=
name|text
operator|.
name|toLowerCase
argument_list|(
name|locale
argument_list|)
expr_stmt|;
comment|//          if (toLowerCase) {
comment|////            use next line once JDK 1.5 String.toLowerCase() performance regression is fixed
comment|////            see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265809
comment|//            text = s.substring(start, i).toLowerCase();
comment|////            char[] chars = new char[i-start];
comment|////            for (int j=start; j< i; j++) chars[j-start] = Character.toLowerCase(s.charAt(j));
comment|////            text = new String(chars);
comment|//          } else {
comment|//            text = s.substring(start, i);
comment|//          }
block|}
block|}
do|while
condition|(
name|text
operator|!=
literal|null
operator|&&
name|isStopWord
argument_list|(
name|text
argument_list|)
condition|)
do|;
name|pos
operator|=
name|i
expr_stmt|;
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|start
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|this
operator|.
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
DECL|method|isTokenChar
specifier|private
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|,
name|boolean
name|isLetter
parameter_list|)
block|{
return|return
name|isLetter
condition|?
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
else|:
operator|!
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
argument_list|)
return|;
block|}
DECL|method|isStopWord
specifier|private
name|boolean
name|isStopWord
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|stopWords
operator|!=
literal|null
operator|&&
name|stopWords
operator|.
name|contains
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
comment|/**    * A StringReader that exposes it's contained string for fast direct access.    * Might make sense to generalize this to CharSequence and make it public?    */
DECL|class|FastStringReader
specifier|static
specifier|final
class|class
name|FastStringReader
extends|extends
name|StringReader
block|{
DECL|field|s
specifier|private
specifier|final
name|String
name|s
decl_stmt|;
DECL|method|FastStringReader
name|FastStringReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getString
name|String
name|getString
parameter_list|()
block|{
return|return
name|s
return|;
block|}
block|}
block|}
end_class

end_unit

