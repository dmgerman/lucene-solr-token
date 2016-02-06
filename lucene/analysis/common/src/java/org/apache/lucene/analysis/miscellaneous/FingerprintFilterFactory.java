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
comment|/**  * Factory for {@link FingerprintFilter}.  *   *<pre class="prettyprint">  * The {@code maxOutputTokenSize} property is optional and defaults to {@code 1024}.    * The {@code separator} property is optional and defaults to the space character.    * See  * {@link FingerprintFilter} for an explanation of its use.  *</pre>  */
end_comment

begin_class
DECL|class|FingerprintFilterFactory
specifier|public
class|class
name|FingerprintFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|MAX_OUTPUT_TOKEN_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_OUTPUT_TOKEN_SIZE_KEY
init|=
literal|"maxOutputTokenSize"
decl_stmt|;
DECL|field|SEPARATOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR_KEY
init|=
literal|"separator"
decl_stmt|;
DECL|field|maxOutputTokenSize
specifier|final
name|int
name|maxOutputTokenSize
decl_stmt|;
DECL|field|separator
specifier|final
name|char
name|separator
decl_stmt|;
comment|/** Creates a new FingerprintFilterFactory */
DECL|method|FingerprintFilterFactory
specifier|public
name|FingerprintFilterFactory
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
name|maxOutputTokenSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
name|MAX_OUTPUT_TOKEN_SIZE_KEY
argument_list|,
name|FingerprintFilter
operator|.
name|DEFAULT_MAX_OUTPUT_TOKEN_SIZE
argument_list|)
expr_stmt|;
name|separator
operator|=
name|getChar
argument_list|(
name|args
argument_list|,
name|SEPARATOR_KEY
argument_list|,
name|FingerprintFilter
operator|.
name|DEFAULT_SEPARATOR
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
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|FingerprintFilter
argument_list|(
name|input
argument_list|,
name|maxOutputTokenSize
argument_list|,
name|separator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

