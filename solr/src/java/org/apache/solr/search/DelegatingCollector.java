begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|Collector
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
name|search
operator|.
name|Scorer
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
comment|/** A simple delegating collector where one can set the delegate after creation */
end_comment

begin_class
DECL|class|DelegatingCollector
specifier|public
class|class
name|DelegatingCollector
extends|extends
name|Collector
block|{
DECL|field|setLastDelegateCount
specifier|static
name|int
name|setLastDelegateCount
decl_stmt|;
comment|// for testing purposes only to determine the number of times a delegating collector chain was used
DECL|field|delegate
specifier|protected
name|Collector
name|delegate
decl_stmt|;
DECL|field|scorer
specifier|protected
name|Scorer
name|scorer
decl_stmt|;
DECL|field|context
specifier|protected
name|IndexReader
operator|.
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|docBase
specifier|protected
name|int
name|docBase
decl_stmt|;
DECL|method|getDelegate
specifier|public
name|Collector
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
DECL|method|setDelegate
specifier|public
name|void
name|setDelegate
parameter_list|(
name|Collector
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
comment|/** Sets the last delegate in a chain of DelegatingCollectors */
DECL|method|setLastDelegate
specifier|public
name|void
name|setLastDelegate
parameter_list|(
name|Collector
name|delegate
parameter_list|)
block|{
name|DelegatingCollector
name|ptr
init|=
name|this
decl_stmt|;
for|for
control|(
init|;
name|ptr
operator|.
name|getDelegate
argument_list|()
operator|instanceof
name|DelegatingCollector
condition|;
name|ptr
operator|=
operator|(
name|DelegatingCollector
operator|)
name|ptr
operator|.
name|getDelegate
argument_list|()
control|)
empty_stmt|;
name|ptr
operator|.
name|setDelegate
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|setLastDelegateCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|delegate
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
operator|.
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
name|delegate
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

