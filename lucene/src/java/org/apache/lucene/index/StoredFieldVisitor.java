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
name|DocumentStoredFieldVisitor
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
name|IndexInput
import|;
end_import

begin_comment
comment|/**  * Expert: provides a low-level means of accessing the stored field  * values in an index.  See {@link IndexReader#document(int,  * StoredFieldVisitor)}.  *  * See {@link DocumentStoredFieldVisitor}, which is a  *<code>StoredFieldVisitor</code> that builds the  * {@link Document} containing all stored fields.  This is  * used by {@link IndexReader#document(int)}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|StoredFieldVisitor
specifier|public
class|class
name|StoredFieldVisitor
block|{
comment|/** Process a binary field.  Note that if you want to    *  skip the field you must seek the IndexInput    *  (e.g., call<code>in.seek(numUTF8Bytes + in.getFilePointer()</code>)    *    *<p>Return true to stop loading fields. */
DECL|method|binaryField
specifier|public
name|boolean
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|numBytes
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/** Process a string field by reading numUTF8Bytes.    *  Note that if you want to skip the field you must    *  seek the IndexInput as if you had read numBytes by    *  (e.g., call<code>in.seek(numUTF8Bytes + in.getFilePointer()</code>)    *    *<p>Return true to stop loading fields. */
DECL|method|stringField
specifier|public
name|boolean
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
name|numUTF8Bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|numUTF8Bytes
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/** Process a int numeric field.    *    *<p>Return true to stop loading fields. */
DECL|method|intField
specifier|public
name|boolean
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
comment|/** Process a long numeric field.    *    *<p>Return true to stop loading fields. */
DECL|method|longField
specifier|public
name|boolean
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
comment|/** Process a float numeric field.    *    *<p>Return true to stop loading fields. */
DECL|method|floatField
specifier|public
name|boolean
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
comment|/** Process a double numeric field.    *    *<p>Return true to stop loading fields. */
DECL|method|doubleField
specifier|public
name|boolean
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

