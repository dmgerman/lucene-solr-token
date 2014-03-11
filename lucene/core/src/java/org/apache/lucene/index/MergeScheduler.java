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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
comment|/**<p>Expert: {@link IndexWriter} uses an instance  *  implementing this interface to execute the merges  *  selected by a {@link MergePolicy}.  The default  *  MergeScheduler is {@link ConcurrentMergeScheduler}.</p>  *<p>Implementers of sub-classes should make sure that {@link #clone()}  *  returns an independent instance able to work with any {@link IndexWriter}  *  instance.</p>  * @lucene.experimental */
end_comment

begin_class
DECL|class|MergeScheduler
specifier|public
specifier|abstract
class|class
name|MergeScheduler
implements|implements
name|Closeable
implements|,
name|Cloneable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|MergeScheduler
specifier|protected
name|MergeScheduler
parameter_list|()
block|{   }
comment|/** Run the merges provided by {@link IndexWriter#getNextMerge()}.    * @param writer the {@link IndexWriter} to obtain the merges from.    * @param trigger the {@link MergeTrigger} that caused this merge to happen    * @param newMergesFound<code>true</code> iff any new merges were found by the caller otherwise<code>false</code>    * */
DECL|method|merge
specifier|public
specifier|abstract
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergeTrigger
name|trigger
parameter_list|,
name|boolean
name|newMergesFound
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Close this MergeScheduler. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergeScheduler
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|MergeScheduler
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

