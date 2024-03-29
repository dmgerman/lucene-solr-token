begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|Message
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
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|NLSException
import|;
end_import

begin_comment
comment|/**  * Error class with NLS support  *   * @see org.apache.lucene.queryparser.flexible.messages.NLS  * @see org.apache.lucene.queryparser.flexible.messages.Message  */
end_comment

begin_class
DECL|class|QueryNodeError
specifier|public
class|class
name|QueryNodeError
extends|extends
name|Error
implements|implements
name|NLSException
block|{
DECL|field|message
specifier|private
name|Message
name|message
decl_stmt|;
comment|/**    * @param message    *          - NLS Message Object    */
DECL|method|QueryNodeError
specifier|public
name|QueryNodeError
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**    * @param throwable    *          - @see java.lang.Error    */
DECL|method|QueryNodeError
specifier|public
name|QueryNodeError
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param message    *          - NLS Message Object    * @param throwable    *          - @see java.lang.Error    */
DECL|method|QueryNodeError
specifier|public
name|QueryNodeError
parameter_list|(
name|Message
name|message
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|.
name|getKey
argument_list|()
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.lucene.messages.NLSException#getMessageObject()    */
annotation|@
name|Override
DECL|method|getMessageObject
specifier|public
name|Message
name|getMessageObject
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
block|}
end_class

end_unit

