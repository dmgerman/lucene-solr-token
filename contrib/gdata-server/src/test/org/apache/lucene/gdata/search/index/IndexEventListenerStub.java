begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|IndexEventListenerStub
specifier|public
class|class
name|IndexEventListenerStub
implements|implements
name|IndexEventListener
block|{
DECL|field|count
name|AtomicInteger
name|count
decl_stmt|;
comment|/**      *       */
DECL|method|IndexEventListenerStub
specifier|public
name|IndexEventListenerStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|count
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexEventListener#commitCallBack(java.lang.String)      */
DECL|method|commitCallBack
specifier|public
name|void
name|commitCallBack
parameter_list|(
name|String
name|service
parameter_list|)
block|{
name|this
operator|.
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|getCalledCount
specifier|public
name|int
name|getCalledCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

