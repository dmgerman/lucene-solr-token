begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ReusableAnalyzerBase
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
comment|/**  * Base class for Analyzers that need to make use of stopword sets.   *   */
end_comment

begin_class
DECL|class|StopwordAnalyzerBase
specifier|public
specifier|abstract
class|class
name|StopwordAnalyzerBase
extends|extends
name|ReusableAnalyzerBase
block|{
comment|/**    * An immutable stopword set    */
DECL|field|stopwords
specifier|protected
specifier|final
name|CharArraySet
name|stopwords
decl_stmt|;
DECL|field|matchVersion
specifier|protected
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Returns the analyzer's stopword set or an empty set if the analyzer has no    * stopwords    *     * @return the analyzer's stopword set or an empty set if the analyzer has no    *         stopwords    */
DECL|method|getStopwordSet
specifier|public
name|Set
argument_list|<
name|?
argument_list|>
name|getStopwordSet
parameter_list|()
block|{
return|return
name|stopwords
return|;
block|}
comment|/**    * Creates a new instance initialized with the given stopword set    *     * @param version    *          the Lucene version for cross version compatibility    * @param stopwords    *          the analyzer's stopword set    */
DECL|method|StopwordAnalyzerBase
specifier|protected
name|StopwordAnalyzerBase
parameter_list|(
specifier|final
name|Version
name|version
parameter_list|,
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|matchVersion
operator|=
name|version
expr_stmt|;
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
name|version
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Analyzer with an empty stopword set    *     * @param version    *          the Lucene version for cross version compatibility    */
DECL|method|StopwordAnalyzerBase
specifier|protected
name|StopwordAnalyzerBase
parameter_list|(
specifier|final
name|Version
name|version
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
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
name|ReusableAnalyzerBase
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
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|wordSet
init|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|aClass
argument_list|,
name|resource
argument_list|,
name|comment
argument_list|)
decl_stmt|;
specifier|final
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|,
name|wordSet
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|wordSet
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

