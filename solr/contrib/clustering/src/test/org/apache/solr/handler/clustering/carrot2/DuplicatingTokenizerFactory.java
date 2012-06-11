begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|LanguageCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|analysis
operator|.
name|ExtendedWhitespaceTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|analysis
operator|.
name|ITokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|ITokenizerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|util
operator|.
name|MutableCharArray
import|;
end_import

begin_class
DECL|class|DuplicatingTokenizerFactory
specifier|public
class|class
name|DuplicatingTokenizerFactory
implements|implements
name|ITokenizerFactory
block|{
annotation|@
name|Override
DECL|method|getTokenizer
specifier|public
name|ITokenizer
name|getTokenizer
parameter_list|(
name|LanguageCode
name|language
parameter_list|)
block|{
return|return
operator|new
name|ITokenizer
argument_list|()
block|{
specifier|private
specifier|final
name|ExtendedWhitespaceTokenizer
name|delegate
init|=
operator|new
name|ExtendedWhitespaceTokenizer
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|MutableCharArray
name|buffer
parameter_list|)
block|{
name|delegate
operator|.
name|setTermBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
operator|+
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|nextToken
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|nextToken
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

