begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|StoredFieldsFormat
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|codecs
operator|.
name|StoredFieldsWriter
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
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41StoredFieldsFormat
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
name|index
operator|.
name|FieldInfo
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
name|index
operator|.
name|FieldInfos
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
name|index
operator|.
name|SegmentInfo
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
name|index
operator|.
name|StorableField
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
name|index
operator|.
name|StoredFieldVisitor
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IOContext
import|;
end_import

begin_comment
comment|/**  * Just like {@link Lucene41StoredFieldsFormat} but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingStoredFieldsFormat
specifier|public
class|class
name|AssertingStoredFieldsFormat
extends|extends
name|StoredFieldsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|StoredFieldsFormat
name|in
init|=
operator|new
name|Lucene41StoredFieldsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|StoredFieldsReader
name|fieldsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingStoredFieldsReader
argument_list|(
name|in
operator|.
name|fieldsReader
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|fn
argument_list|,
name|context
argument_list|)
argument_list|,
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|StoredFieldsWriter
name|fieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingStoredFieldsWriter
argument_list|(
name|in
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AssertingStoredFieldsReader
specifier|static
class|class
name|AssertingStoredFieldsReader
extends|extends
name|StoredFieldsReader
block|{
DECL|field|in
specifier|private
specifier|final
name|StoredFieldsReader
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingStoredFieldsReader
name|AssertingStoredFieldsReader
parameter_list|(
name|StoredFieldsReader
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitDocument
specifier|public
name|void
name|visitDocument
parameter_list|(
name|int
name|n
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|n
operator|>=
literal|0
operator|&&
name|n
operator|<
name|maxDoc
assert|;
name|in
operator|.
name|visitDocument
argument_list|(
name|n
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|StoredFieldsReader
name|clone
parameter_list|()
block|{
return|return
operator|new
name|AssertingStoredFieldsReader
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|in
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
DECL|enum|Status
enum|enum
name|Status
block|{
DECL|enum constant|UNDEFINED
DECL|enum constant|STARTED
DECL|enum constant|FINISHED
name|UNDEFINED
block|,
name|STARTED
block|,
name|FINISHED
block|;   }
DECL|class|AssertingStoredFieldsWriter
specifier|static
class|class
name|AssertingStoredFieldsWriter
extends|extends
name|StoredFieldsWriter
block|{
DECL|field|in
specifier|private
specifier|final
name|StoredFieldsWriter
name|in
decl_stmt|;
DECL|field|numWritten
specifier|private
name|int
name|numWritten
decl_stmt|;
DECL|field|docStatus
specifier|private
name|Status
name|docStatus
decl_stmt|;
DECL|method|AssertingStoredFieldsWriter
name|AssertingStoredFieldsWriter
parameter_list|(
name|StoredFieldsWriter
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|docStatus
operator|=
name|Status
operator|.
name|UNDEFINED
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|numWritten
operator|++
expr_stmt|;
name|docStatus
operator|=
name|Status
operator|.
name|STARTED
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
name|docStatus
operator|=
name|Status
operator|.
name|FINISHED
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|StorableField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|writeField
argument_list|(
name|info
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|in
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|==
operator|(
name|numDocs
operator|>
literal|0
condition|?
name|Status
operator|.
name|FINISHED
else|:
name|Status
operator|.
name|UNDEFINED
operator|)
assert|;
name|in
operator|.
name|finish
argument_list|(
name|fis
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
assert|assert
name|numDocs
operator|==
name|numWritten
assert|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

