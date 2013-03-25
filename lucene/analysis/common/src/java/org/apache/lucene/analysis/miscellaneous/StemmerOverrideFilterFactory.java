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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|List
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
name|miscellaneous
operator|.
name|StemmerOverrideFilter
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
name|StemmerOverrideFilter
operator|.
name|StemmerOverrideMap
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|CharsRef
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
name|fst
operator|.
name|FST
import|;
end_import

begin_comment
comment|/**  * Factory for {@link StemmerOverrideFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_dicstem" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.StemmerOverrideFilterFactory" dictionary="dictionary.txt" ignoreCase="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_class
DECL|class|StemmerOverrideFilterFactory
specifier|public
class|class
name|StemmerOverrideFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|dictionary
specifier|private
name|StemmerOverrideMap
name|dictionary
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
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
name|dictionaryFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"dictionary"
argument_list|)
decl_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictionaryFiles
operator|!=
literal|null
condition|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|dictionaryFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
name|ignoreCase
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|getLines
argument_list|(
name|loader
argument_list|,
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|list
control|)
block|{
name|String
index|[]
name|mapping
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|mapping
index|[
literal|0
index|]
argument_list|,
name|mapping
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|dictionary
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|isIgnoreCase
specifier|public
name|boolean
name|isIgnoreCase
parameter_list|()
block|{
return|return
name|ignoreCase
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
name|dictionary
operator|==
literal|null
condition|?
name|input
else|:
operator|new
name|StemmerOverrideFilter
argument_list|(
name|input
argument_list|,
name|dictionary
argument_list|)
return|;
block|}
block|}
end_class

end_unit

