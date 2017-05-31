begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

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
name|TokenizerFactory
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
name|AttributeFactory
import|;
end_import

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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
operator|.
name|MAX_TOKEN_LENGTH_LIMIT
import|;
end_import

begin_comment
comment|/**  * Factory for {@link KeywordTokenizer}.   *<pre class="prettyprint">  *&lt;fieldType name="text_keyword" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory" maxTokenLen="256"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *  * Options:  *<ul>  *<li>maxTokenLen: max token length, should be greater than 0 and less than   *        MAX_TOKEN_LENGTH_LIMIT (1024*1024). It is rare to need to change this  *      else {@link KeywordTokenizer}::DEFAULT_BUFFER_SIZE</li>  *</ul>  */
end_comment

begin_class
DECL|class|KeywordTokenizerFactory
specifier|public
class|class
name|KeywordTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|maxTokenLen
specifier|private
specifier|final
name|int
name|maxTokenLen
decl_stmt|;
comment|/** Creates a new KeywordTokenizerFactory */
DECL|method|KeywordTokenizerFactory
specifier|public
name|KeywordTokenizerFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|maxTokenLen
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"maxTokenLen"
argument_list|,
name|KeywordTokenizer
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxTokenLen
operator|>
name|MAX_TOKEN_LENGTH_LIMIT
operator|||
name|maxTokenLen
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokenLen must be greater than 0 and less than "
operator|+
name|MAX_TOKEN_LENGTH_LIMIT
operator|+
literal|" passed: "
operator|+
name|maxTokenLen
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|KeywordTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
return|return
operator|new
name|KeywordTokenizer
argument_list|(
name|factory
argument_list|,
name|maxTokenLen
argument_list|)
return|;
block|}
block|}
end_class

end_unit

