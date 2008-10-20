begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
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
name|File
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Hashtable
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
name|WordlistLoader
import|;
end_import

begin_comment
comment|/**  * Analyzer for Arabic.   *<p>  * This analyzer implements light-stemming as specified by:  *<i>  * Improving Stemming for Arabic Information Retrieval:   *      Light Stemming and Co-occurrence Analysis  *</i>      * http://ciir.cs.umass.edu/pubfiles/ir-249.pdf  *<p>  * The analysis package contains three primary components:  *<ul>  *<li>{@link ArabicNormalizationFilter}: Arabic orthographic normalization.  *<li>{@link ArabicStemFilter}: Arabic light stemming  *<li>Arabic stop words file: a set of default Arabic stop words.  *</ul>  *   */
end_comment

begin_class
DECL|class|ArabicAnalyzer
specifier|public
specifier|final
class|class
name|ArabicAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * File containing default Arabic stopwords.    *     * Default stopword list is from http://members.unine.ch/jacques.savoy/clef/index.html    * The stopword list is BSD-Licensed.    */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
name|Set
name|stoptable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * The comment character in the stopwords file.  All lines prefixed with this will be ignored      */
DECL|field|STOPWORDS_COMMENT
specifier|public
specifier|static
specifier|final
name|String
name|STOPWORDS_COMMENT
init|=
literal|"#"
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words: {@link #DEFAULT_STOPWORD_FILE}.    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|()
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|ArabicAnalyzer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|DEFAULT_STOPWORD_FILE
argument_list|)
decl_stmt|;
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.  Lines can be commented out using {@link #STOPWORDS_COMMENT}    */
DECL|method|ArabicAnalyzer
specifier|public
name|ArabicAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a TokenStream which tokenizes all the text in the provided Reader.    *    * @return  A TokenStream build from a StandardTokenizer filtered with    * 			StandardFilter, StopFilter, ArabicNormalizationFilter and ArabicStemFilter.    */
DECL|method|tokenStream
specifier|public
specifier|final
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
name|TokenStream
name|result
init|=
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ArabicNormalizationFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ArabicStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

