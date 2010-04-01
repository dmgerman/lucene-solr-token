begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|payloads
operator|.
name|DelimitedPayloadTokenFilter
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
name|payloads
operator|.
name|PayloadEncoder
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
name|payloads
operator|.
name|FloatEncoder
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
name|payloads
operator|.
name|IntegerEncoder
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
name|payloads
operator|.
name|IdentityEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|ResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|ResourceLoaderAware
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

begin_comment
comment|/**  *  * Factory for {@link DelimitedPayloadTokenFilter}  **/
end_comment

begin_class
DECL|class|DelimitedPayloadTokenFilterFactory
specifier|public
class|class
name|DelimitedPayloadTokenFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|ENCODER_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|ENCODER_ATTR
init|=
literal|"encoder"
decl_stmt|;
DECL|field|DELIMITER_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|DELIMITER_ATTR
init|=
literal|"delimiter"
decl_stmt|;
DECL|field|encoder
specifier|private
name|PayloadEncoder
name|encoder
decl_stmt|;
DECL|field|delimiter
specifier|private
name|char
name|delimiter
init|=
literal|'|'
decl_stmt|;
DECL|method|create
specifier|public
name|DelimitedPayloadTokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|DelimitedPayloadTokenFilter
argument_list|(
name|input
argument_list|,
name|delimiter
argument_list|,
name|encoder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
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
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|String
name|encoderClass
init|=
name|args
operator|.
name|get
argument_list|(
name|ENCODER_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoderClass
operator|.
name|equals
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
name|encoder
operator|=
operator|new
name|FloatEncoder
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|encoderClass
operator|.
name|equals
argument_list|(
literal|"integer"
argument_list|)
condition|)
block|{
name|encoder
operator|=
operator|new
name|IntegerEncoder
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|encoderClass
operator|.
name|equals
argument_list|(
literal|"identity"
argument_list|)
condition|)
block|{
name|encoder
operator|=
operator|new
name|IdentityEncoder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|encoder
operator|=
operator|(
name|PayloadEncoder
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|encoderClass
argument_list|)
expr_stmt|;
block|}
name|String
name|delim
init|=
name|args
operator|.
name|get
argument_list|(
name|DELIMITER_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|delim
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|delim
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|delimiter
operator|=
name|delim
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Delimiter must be one character only"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

