begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
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
name|HashSet
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
name|Set
name|EXTENDED_ENGLISH_STOP_WORDS
decl_stmt|;
static|static
block|{
name|EXTENDED_ENGLISH_STOP_WORDS
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|EXTENDED_ENGLISH_STOP_WORDS
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"about"
block|,
literal|"above"
block|,
literal|"across"
block|,
literal|"adj"
block|,
literal|"after"
block|,
literal|"afterwards"
block|,
literal|"again"
block|,
literal|"against"
block|,
literal|"albeit"
block|,
literal|"all"
block|,
literal|"almost"
block|,
literal|"alone"
block|,
literal|"along"
block|,
literal|"already"
block|,
literal|"also"
block|,
literal|"although"
block|,
literal|"always"
block|,
literal|"among"
block|,
literal|"amongst"
block|,
literal|"an"
block|,
literal|"and"
block|,
literal|"another"
block|,
literal|"any"
block|,
literal|"anyhow"
block|,
literal|"anyone"
block|,
literal|"anything"
block|,
literal|"anywhere"
block|,
literal|"are"
block|,
literal|"around"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"became"
block|,
literal|"because"
block|,
literal|"become"
block|,
literal|"becomes"
block|,
literal|"becoming"
block|,
literal|"been"
block|,
literal|"before"
block|,
literal|"beforehand"
block|,
literal|"behind"
block|,
literal|"being"
block|,
literal|"below"
block|,
literal|"beside"
block|,
literal|"besides"
block|,
literal|"between"
block|,
literal|"beyond"
block|,
literal|"both"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"can"
block|,
literal|"cannot"
block|,
literal|"co"
block|,
literal|"could"
block|,
literal|"down"
block|,
literal|"during"
block|,
literal|"each"
block|,
literal|"eg"
block|,
literal|"either"
block|,
literal|"else"
block|,
literal|"elsewhere"
block|,
literal|"enough"
block|,
literal|"etc"
block|,
literal|"even"
block|,
literal|"ever"
block|,
literal|"every"
block|,
literal|"everyone"
block|,
literal|"everything"
block|,
literal|"everywhere"
block|,
literal|"except"
block|,
literal|"few"
block|,
literal|"first"
block|,
literal|"for"
block|,
literal|"former"
block|,
literal|"formerly"
block|,
literal|"from"
block|,
literal|"further"
block|,
literal|"had"
block|,
literal|"has"
block|,
literal|"have"
block|,
literal|"he"
block|,
literal|"hence"
block|,
literal|"her"
block|,
literal|"here"
block|,
literal|"hereafter"
block|,
literal|"hereby"
block|,
literal|"herein"
block|,
literal|"hereupon"
block|,
literal|"hers"
block|,
literal|"herself"
block|,
literal|"him"
block|,
literal|"himself"
block|,
literal|"his"
block|,
literal|"how"
block|,
literal|"however"
block|,
literal|"i"
block|,
literal|"ie"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"inc"
block|,
literal|"indeed"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"its"
block|,
literal|"itself"
block|,
literal|"last"
block|,
literal|"latter"
block|,
literal|"latterly"
block|,
literal|"least"
block|,
literal|"less"
block|,
literal|"ltd"
block|,
literal|"many"
block|,
literal|"may"
block|,
literal|"me"
block|,
literal|"meanwhile"
block|,
literal|"might"
block|,
literal|"more"
block|,
literal|"moreover"
block|,
literal|"most"
block|,
literal|"mostly"
block|,
literal|"much"
block|,
literal|"must"
block|,
literal|"my"
block|,
literal|"myself"
block|,
literal|"namely"
block|,
literal|"neither"
block|,
literal|"never"
block|,
literal|"nevertheless"
block|,
literal|"next"
block|,
literal|"no"
block|,
literal|"nobody"
block|,
literal|"none"
block|,
literal|"noone"
block|,
literal|"nor"
block|,
literal|"not"
block|,
literal|"nothing"
block|,
literal|"now"
block|,
literal|"nowhere"
block|,
literal|"of"
block|,
literal|"off"
block|,
literal|"often"
block|,
literal|"on"
block|,
literal|"once one"
block|,
literal|"only"
block|,
literal|"onto"
block|,
literal|"or"
block|,
literal|"other"
block|,
literal|"others"
block|,
literal|"otherwise"
block|,
literal|"our"
block|,
literal|"ours"
block|,
literal|"ourselves"
block|,
literal|"out"
block|,
literal|"over"
block|,
literal|"own"
block|,
literal|"per"
block|,
literal|"perhaps"
block|,
literal|"rather"
block|,
literal|"s"
block|,
literal|"same"
block|,
literal|"seem"
block|,
literal|"seemed"
block|,
literal|"seeming"
block|,
literal|"seems"
block|,
literal|"several"
block|,
literal|"she"
block|,
literal|"should"
block|,
literal|"since"
block|,
literal|"so"
block|,
literal|"some"
block|,
literal|"somehow"
block|,
literal|"someone"
block|,
literal|"something"
block|,
literal|"sometime"
block|,
literal|"sometimes"
block|,
literal|"somewhere"
block|,
literal|"still"
block|,
literal|"such"
block|,
literal|"t"
block|,
literal|"than"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"them"
block|,
literal|"themselves"
block|,
literal|"then"
block|,
literal|"thence"
block|,
literal|"there"
block|,
literal|"thereafter"
block|,
literal|"thereby"
block|,
literal|"therefor"
block|,
literal|"therein"
block|,
literal|"thereupon"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"those"
block|,
literal|"though"
block|,
literal|"through"
block|,
literal|"throughout"
block|,
literal|"thru"
block|,
literal|"thus"
block|,
literal|"to"
block|,
literal|"together"
block|,
literal|"too"
block|,
literal|"toward"
block|,
literal|"towards"
block|,
literal|"under"
block|,
literal|"until"
block|,
literal|"up"
block|,
literal|"upon"
block|,
literal|"us"
block|,
literal|"very"
block|,
literal|"via"
block|,
literal|"was"
block|,
literal|"we"
block|,
literal|"well"
block|,
literal|"were"
block|,
literal|"what"
block|,
literal|"whatever"
block|,
literal|"whatsoever"
block|,
literal|"when"
block|,
literal|"whence"
block|,
literal|"whenever"
block|,
literal|"whensoever"
block|,
literal|"where"
block|,
literal|"whereafter"
block|,
literal|"whereas"
block|,
literal|"whereat"
block|,
literal|"whereby"
block|,
literal|"wherefrom"
block|,
literal|"wherein"
block|,
literal|"whereinto"
block|,
literal|"whereof"
block|,
literal|"whereon"
block|,
literal|"whereto"
block|,
literal|"whereunto"
block|,
literal|"whereupon"
block|,
literal|"wherever"
block|,
literal|"wherewith"
block|,
literal|"whether"
block|,
literal|"which"
block|,
literal|"whichever"
block|,
literal|"whichsoever"
block|,
literal|"while"
block|,
literal|"whilst"
block|,
literal|"whither"
block|,
literal|"who"
block|,
literal|"whoever"
block|,
literal|"whole"
block|,
literal|"whom"
block|,
literal|"whomever"
block|,
literal|"whomsoever"
block|,
literal|"whose"
block|,
literal|"whosoever"
block|,
literal|"why"
block|,
literal|"will"
block|,
literal|"with"
block|,
literal|"within"
block|,
literal|"without"
block|,
literal|"would"
block|,
literal|"xsubj"
block|,
literal|"xcal"
block|,
literal|"xauthor"
block|,
literal|"xother "
block|,
literal|"xnote"
block|,
literal|"yet"
block|,
literal|"you"
block|,
literal|"your"
block|,
literal|"yours"
block|,
literal|"yourself"
block|,
literal|"yourselves"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
comment|/** somewhat oversized to minimize hash collisions */
DECL|method|makeStopSet
specifier|private
specifier|static
name|Set
name|makeStopSet
parameter_list|(
name|Set
name|stopWords
parameter_list|)
block|{
name|Set
name|stops
init|=
operator|new
name|HashSet
argument_list|(
name|stopWords
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|,
literal|0.3f
argument_list|)
decl_stmt|;
name|stops
operator|.
name|addAll
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
return|return
name|stops
return|;
comment|//    return Collections.unmodifiableSet(stops);
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

