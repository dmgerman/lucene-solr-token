begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
name|TokenFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import

begin_comment
comment|/**  * Factory for {@link SnowballFilter}, with configurable language  *<p>  * Note: Use of the "Lovins" stemmer is not recommended, as it is implemented with reflection.  *<pre class="prettyprint">  *&lt;fieldType name="text_snowballstem" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.LowerCaseFilterFactory"/&gt;  *&lt;filter class="solr.SnowballPorterFilterFactory" protected="protectedkeyword.txt" language="English"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *  */
end_comment

begin_class
DECL|class|SnowballPorterFilterFactory
specifier|public
class|class
name|SnowballPorterFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|PROTECTED_TOKENS
specifier|public
specifier|static
specifier|final
name|String
name|PROTECTED_TOKENS
init|=
literal|"protected"
decl_stmt|;
DECL|field|language
specifier|private
name|String
name|language
init|=
literal|"English"
decl_stmt|;
DECL|field|stemClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|SnowballProgram
argument_list|>
name|stemClass
decl_stmt|;
DECL|field|protectedWords
specifier|private
name|CharArraySet
name|protectedWords
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|cfgLanguage
init|=
name|args
operator|.
name|get
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfgLanguage
operator|!=
literal|null
condition|)
name|language
operator|=
name|cfgLanguage
expr_stmt|;
name|String
name|className
init|=
literal|"org.tartarus.snowball.ext."
operator|+
name|language
operator|+
literal|"Stemmer"
decl_stmt|;
name|stemClass
operator|=
name|loader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
name|SnowballProgram
operator|.
name|class
argument_list|)
operator|.
name|getClass
argument_list|()
expr_stmt|;
name|String
name|wordFiles
init|=
name|args
operator|.
name|get
argument_list|(
name|PROTECTED_TOKENS
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordFiles
operator|!=
literal|null
condition|)
block|{
name|protectedWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|wordFiles
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|SnowballProgram
name|program
decl_stmt|;
try|try
block|{
name|program
operator|=
name|stemClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error instantiating stemmer for language "
operator|+
name|language
operator|+
literal|"from class "
operator|+
name|stemClass
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|protectedWords
operator|!=
literal|null
condition|)
name|input
operator|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|input
argument_list|,
name|protectedWords
argument_list|)
expr_stmt|;
return|return
operator|new
name|SnowballFilter
argument_list|(
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
block|}
end_class

end_unit

