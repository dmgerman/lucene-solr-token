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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
import|;
end_import

begin_comment
comment|/** Writes bytes through to a primary IndexOutput, computing  *  checksum.  Note that you cannot use seek().*/
end_comment

begin_class
DECL|class|ChecksumIndexOutput
specifier|public
class|class
name|ChecksumIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|main
name|IndexOutput
name|main
decl_stmt|;
DECL|field|digest
name|Checksum
name|digest
decl_stmt|;
DECL|method|ChecksumIndexOutput
specifier|public
name|ChecksumIndexOutput
parameter_list|(
name|IndexOutput
name|main
parameter_list|)
block|{
name|this
operator|.
name|main
operator|=
name|main
expr_stmt|;
name|digest
operator|=
operator|new
name|CRC32
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|main
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|main
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
block|{
return|return
name|digest
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|main
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|main
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|main
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not allowed"
argument_list|)
throw|;
block|}
comment|/**    * Starts but does not complete the commit of this file (=    * writing of the final checksum at the end).  After this    * is called must call {@link #finishCommit} and the    * {@link #close} to complete the commit.    */
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|checksum
init|=
name|getChecksum
argument_list|()
decl_stmt|;
comment|// Intentionally write a mismatched checksum.  This is
comment|// because we want to 1) test, as best we can, that we
comment|// are able to write a long to the file, but 2) not
comment|// actually "commit" the file yet.  This (prepare
comment|// commit) is phase 1 of a two-phase commit.
specifier|final
name|long
name|pos
init|=
name|main
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|main
operator|.
name|writeLong
argument_list|(
name|checksum
operator|-
literal|1
argument_list|)
expr_stmt|;
name|main
operator|.
name|flush
argument_list|()
expr_stmt|;
name|main
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/** See {@link #prepareCommit} */
DECL|method|finishCommit
specifier|public
name|void
name|finishCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|main
operator|.
name|writeLong
argument_list|(
name|getChecksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|main
operator|.
name|length
argument_list|()
return|;
block|}
block|}
end_class

end_unit

