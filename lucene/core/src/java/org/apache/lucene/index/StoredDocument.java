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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|DoubleField
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
name|FieldType
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
name|FloatField
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
name|IntField
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
name|LongField
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
name|IndexSearcher
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
name|ScoreDoc
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * StoredDocument is retrieved from IndexReader containing only stored fields from indexed {@link IndexDocument}. */
end_comment

begin_class
DECL|class|StoredDocument
specifier|public
class|class
name|StoredDocument
implements|implements
name|Iterable
argument_list|<
name|StorableField
argument_list|>
block|{
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|StorableField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|StorableField
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|StorableField
name|field
parameter_list|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|getFields
specifier|public
name|StorableField
index|[]
name|getFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|StorableField
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|StorableField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|StorableField
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns a field with the given name if any exist in this document, or    * null.  If multiple fields exists with this name, this method returns the    * first value added.    */
DECL|method|getField
specifier|public
specifier|final
name|StorableField
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns a List of all the fields in a document.    *<p>Note that fields which are<i>not</i> stored are    *<i>not</i> available in documents retrieved from the    * index, e.g. {@link IndexSearcher#doc(int)} or {@link    * IndexReader#document(int)}.    *     * @return an immutable<code>List[StorableField]</code>     */
DECL|method|getFields
specifier|public
specifier|final
name|List
argument_list|<
name|StorableField
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|this
operator|.
name|fields
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Returns an array of byte arrays for of the fields that have the name specified    * as the method parameter.  This method returns an empty    * array when there are no matching fields.  It never    * returns null.    *    * @param name the name of the field    * @return a<code>byte[][]</code> of binary field values    */
DECL|method|getBinaryValues
specifier|public
specifier|final
name|BytesRef
index|[]
name|getBinaryValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Returns an array of bytes for the first (or only) field that has the name    * specified as the method parameter. This method will return<code>null</code>    * if no binary fields with the specified name are available.    * There may be non-binary fields with the same name.    *    * @param name the name of the field.    * @return a<code>byte[]</code> containing the binary field value or<code>null</code>    */
DECL|method|getBinaryValue
specifier|public
specifier|final
name|BytesRef
name|getBinaryValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
name|bytes
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|field|NO_STRINGS
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|NO_STRINGS
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
comment|/**     * Returns an array of values of the field specified as the method parameter.     * This method returns an empty array when there are no     * matching fields.  It never returns null.     * For {@link IntField}, {@link LongField}, {@link     * FloatField} and {@link DoubleField} it returns the string value of the number. If you want     * the actual numeric field instances back, use {@link #getFields}.     * @param name the name of the field     * @return a<code>String[]</code> of field values     */
DECL|method|getValues
specifier|public
specifier|final
name|String
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|NO_STRINGS
return|;
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns the string value of the field with the given name if any exist in     * this document, or null.  If multiple fields exist with this name, this     * method returns the first value added. If only binary fields with this name     * exist, returns null.     * For {@link IntField}, {@link LongField}, {@link     * FloatField} and {@link DoubleField} it returns the string value of the number. If you want     * the actual numeric field instance back, use {@link #getField}.     */
DECL|method|get
specifier|public
specifier|final
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|StorableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Prints the fields of a document for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"StoredDocument<"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StorableField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|fields
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

