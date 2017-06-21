begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link DelimitedTermFrequencyTokenFilter}. The field must have {@code omitPositions=true}.  *<pre class="prettyprint">  *&lt;fieldType name="text_tfdl" class="solr.TextField" omitPositions="true"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.DelimitedTermFrequencyTokenFilterFactory" delimiter="|"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|DelimitedTermFrequencyTokenFilterFactory
specifier|public
class|class
name|DelimitedTermFrequencyTokenFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|DELIMITER_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|DELIMITER_ATTR
init|=
literal|"delimiter"
decl_stmt|;
DECL|field|delimiter
specifier|private
specifier|final
name|char
name|delimiter
decl_stmt|;
comment|/** Creates a new DelimitedPayloadTokenFilterFactory */
DECL|method|DelimitedTermFrequencyTokenFilterFactory
specifier|public
name|DelimitedTermFrequencyTokenFilterFactory
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
name|delimiter
operator|=
name|getChar
argument_list|(
name|args
argument_list|,
name|DELIMITER_ATTR
argument_list|,
name|DelimitedTermFrequencyTokenFilter
operator|.
name|DEFAULT_DELIMITER
argument_list|)
expr_stmt|;
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
name|DelimitedTermFrequencyTokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|DelimitedTermFrequencyTokenFilter
argument_list|(
name|input
argument_list|,
name|delimiter
argument_list|)
return|;
block|}
block|}
end_class

end_unit
