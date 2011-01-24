begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Used by DocumentsWriter to maintain per-thread state.  *  We keep a separate Posting hash and other state for each  *  thread and then merge postings hashes from all threads  *  when writing the segment. */
end_comment

begin_class
DECL|class|DocumentsWriterThreadState
specifier|final
class|class
name|DocumentsWriterThreadState
block|{
DECL|field|isIdle
name|boolean
name|isIdle
init|=
literal|true
decl_stmt|;
comment|// false if this is currently in use by a thread
DECL|field|numThreads
name|int
name|numThreads
init|=
literal|1
decl_stmt|;
comment|// Number of threads that share this instance
DECL|field|consumer
specifier|final
name|DocConsumerPerThread
name|consumer
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|docWriter
specifier|final
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|method|DocumentsWriterThreadState
specifier|public
name|DocumentsWriterThreadState
parameter_list|(
name|DocumentsWriter
name|docWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|docState
operator|=
operator|new
name|DocumentsWriter
operator|.
name|DocState
argument_list|()
expr_stmt|;
name|docState
operator|.
name|infoStream
operator|=
name|docWriter
operator|.
name|infoStream
expr_stmt|;
name|docState
operator|.
name|similarityProvider
operator|=
name|docWriter
operator|.
name|similarityProvider
expr_stmt|;
name|docState
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|consumer
operator|=
name|docWriter
operator|.
name|consumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|doAfterFlush
name|void
name|doAfterFlush
parameter_list|()
block|{
name|numThreads
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

