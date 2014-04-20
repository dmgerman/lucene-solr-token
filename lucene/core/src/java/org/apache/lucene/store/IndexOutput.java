begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
comment|/** Abstract base class for output to a file in a Directory.  A random-access  * output stream.  Used for all Lucene index output operations.    *<p>{@code IndexOutput} may only be used from one thread, because it is not  * thread safe (it keeps internal state like file position).    * @see Directory  * @see IndexInput  */
end_comment

begin_class
DECL|class|IndexOutput
specifier|public
specifier|abstract
class|class
name|IndexOutput
extends|extends
name|DataOutput
implements|implements
name|Closeable
block|{
comment|/** Closes this stream to further operations. */
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
comment|/** Returns the current position in this file, where the next write will    * occur.    */
DECL|method|getFilePointer
specifier|public
specifier|abstract
name|long
name|getFilePointer
parameter_list|()
function_decl|;
comment|/** Returns the current checksum of bytes written so far */
DECL|method|getChecksum
specifier|public
specifier|abstract
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

