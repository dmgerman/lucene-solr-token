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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|OutputStream
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
import|;
end_import

begin_class
DECL|class|FieldsWriter
specifier|final
class|class
name|FieldsWriter
block|{
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|OutputStream
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
name|OutputStream
name|indexStream
decl_stmt|;
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|fieldsStream
operator|=
name|d
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".fdt"
argument_list|)
expr_stmt|;
name|indexStream
operator|=
name|d
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".fdx"
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDocument
specifier|final
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|storedCount
init|=
literal|0
decl_stmt|;
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|storedCount
operator|++
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|storedCount
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|fields
argument_list|()
expr_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
name|bits
operator||=
literal|1
expr_stmt|;
name|fieldsStream
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeString
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

