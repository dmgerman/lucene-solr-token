begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Normalizes token text to lower case.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|LowerCaseFilter
specifier|public
specifier|final
class|class
name|LowerCaseFilter
extends|extends
name|TokenFilter
block|{
DECL|method|LowerCaseFilter
specifier|public
name|LowerCaseFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|.
name|termText
operator|=
name|t
operator|.
name|termText
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

