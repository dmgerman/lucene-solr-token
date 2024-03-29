begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Base class for Analyzers that need to make use of stopword sets.   *   */
end_comment

begin_class
DECL|class|StopwordAnalyzerBase
specifier|public
specifier|abstract
class|class
name|StopwordAnalyzerBase
extends|extends
name|Analyzer
block|{
comment|/**    * An immutable stopword set    */
DECL|field|stopwords
specifier|protected
specifier|final
name|CharArraySet
name|stopwords
decl_stmt|;
comment|/**    * Returns the analyzer's stopword set or an empty set if the analyzer has no    * stopwords    *     * @return the analyzer's stopword set or an empty set if the analyzer has no    *         stopwords    */
DECL|method|getStopwordSet
specifier|public
name|CharArraySet
name|getStopwordSet
parameter_list|()
block|{
return|return
name|stopwords
return|;
block|}
comment|/**    * Creates a new instance initialized with the given stopword set    *     * @param stopwords    *          the analyzer's stopword set    */
DECL|method|StopwordAnalyzerBase
specifier|protected
name|StopwordAnalyzerBase
parameter_list|(
specifier|final
name|CharArraySet
name|stopwords
parameter_list|)
block|{
comment|// analyzers should use char array set for stopwords!
name|this
operator|.
name|stopwords
operator|=
name|stopwords
operator|==
literal|null
condition|?
name|CharArraySet
operator|.
name|EMPTY_SET
else|:
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Analyzer with an empty stopword set    */
DECL|method|StopwordAnalyzerBase
specifier|protected
name|StopwordAnalyzerBase
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a CharArraySet from a file resource associated with a class. (See    * {@link Class#getResourceAsStream(String)}).    *     * @param ignoreCase    *<code>true</code> if the set should ignore the case of the    *          stopwords, otherwise<code>false</code>    * @param aClass    *          a class that is associated with the given stopwordResource    * @param resource    *          name of the resource file associated with the given class    * @param comment    *          comment string to ignore in the stopword file    * @return a CharArraySet containing the distinct stopwords from the given    *         file    * @throws IOException    *           if loading the stopwords throws an {@link IOException}    */
DECL|method|loadStopwordSet
specifier|protected
specifier|static
name|CharArraySet
name|loadStopwordSet
parameter_list|(
specifier|final
name|boolean
name|ignoreCase
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|aClass
parameter_list|,
specifier|final
name|String
name|resource
parameter_list|,
specifier|final
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|aClass
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
return|return
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|comment
argument_list|,
operator|new
name|CharArraySet
argument_list|(
literal|16
argument_list|,
name|ignoreCase
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a CharArraySet from a path.    *     * @param stopwords    *          the stopwords file to load    * @return a CharArraySet containing the distinct stopwords from the given    *         file    * @throws IOException    *           if loading the stopwords throws an {@link IOException}    */
DECL|method|loadStopwordSet
specifier|protected
specifier|static
name|CharArraySet
name|loadStopwordSet
parameter_list|(
name|Path
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|stopwords
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
return|return
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a CharArraySet from a file.    *     * @param stopwords    *          the stopwords reader to load    *     * @return a CharArraySet containing the distinct stopwords from the given    *         reader    * @throws IOException    *           if loading the stopwords throws an {@link IOException}    */
DECL|method|loadStopwordSet
specifier|protected
specifier|static
name|CharArraySet
name|loadStopwordSet
parameter_list|(
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

