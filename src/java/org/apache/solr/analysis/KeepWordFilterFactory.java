begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|Config
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
name|Set
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

begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|KeepWordFilterFactory
specifier|public
class|class
name|KeepWordFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|words
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|words
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|String
name|wordFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|"words"
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
name|wordFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|Config
operator|.
name|getLines
argument_list|(
name|wordFile
argument_list|)
decl_stmt|;
name|words
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
operator|(
name|String
index|[]
operator|)
name|wlist
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
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
block|}
comment|/**    * Set the keep word list.    * NOTE: if ignoreCase==true, the words are expected to be lowercase    */
DECL|method|setWords
specifier|public
name|void
name|setWords
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|words
parameter_list|)
block|{
name|this
operator|.
name|words
operator|=
name|words
expr_stmt|;
block|}
DECL|method|setIgnoreCase
specifier|public
name|void
name|setIgnoreCase
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
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
name|KeepWordFilter
argument_list|(
name|input
argument_list|,
name|words
argument_list|,
name|ignoreCase
argument_list|)
return|;
block|}
block|}
end_class

end_unit

