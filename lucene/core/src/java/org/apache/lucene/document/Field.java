begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|BytesTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|IndexOptions
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
name|IndexableField
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
name|IndexableFieldType
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
comment|/**  * Expert: directly create a field for a document.  Most  * users should use one of the sugar subclasses:   *<ul>  *<li>{@link TextField}: {@link Reader} or {@link String} indexed for full-text search  *<li>{@link StringField}: {@link String} indexed verbatim as a single token  *<li>{@link IntPoint}: {@code int} indexed for exact/range queries.  *<li>{@link LongPoint}: {@code long} indexed for exact/range queries.  *<li>{@link FloatPoint}: {@code float} indexed for exact/range queries.  *<li>{@link DoublePoint}: {@code double} indexed for exact/range queries.  *<li>{@link SortedDocValuesField}: {@code byte[]} indexed column-wise for sorting/faceting  *<li>{@link SortedSetDocValuesField}: {@code SortedSet<byte[]>} indexed column-wise for sorting/faceting  *<li>{@link NumericDocValuesField}: {@code long} indexed column-wise for sorting/faceting  *<li>{@link SortedNumericDocValuesField}: {@code SortedSet<long>} indexed column-wise for sorting/faceting  *<li>{@link StoredField}: Stored-only value for retrieving in summary results  *</ul>  *  *<p> A field is a section of a Document. Each field has three  * parts: name, type and value. Values may be text  * (String, Reader or pre-analyzed TokenStream), binary  * (byte[]), or numeric (a Number).  Fields are optionally stored in the  * index, so that they may be returned with hits on the document.  *  *<p>  * NOTE: the field type is an {@link IndexableFieldType}.  Making changes  * to the state of the IndexableFieldType will impact any  * Field it is used in.  It is strongly recommended that no  * changes be made after Field instantiation.  */
end_comment

begin_class
DECL|class|Field
specifier|public
class|class
name|Field
implements|implements
name|IndexableField
block|{
comment|/**    * Field's type    */
DECL|field|type
specifier|protected
specifier|final
name|FieldType
name|type
decl_stmt|;
comment|/**    * Field's name    */
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Field's value */
DECL|field|fieldsData
specifier|protected
name|Object
name|fieldsData
decl_stmt|;
comment|/** Pre-analyzed tokenStream for indexed fields; this is    * separate from fieldsData because you are allowed to    * have both; eg maybe field has a String value but you    * customize how it's tokenized */
DECL|field|tokenStream
specifier|protected
name|TokenStream
name|tokenStream
decl_stmt|;
comment|/**    * Field's boost    * @see #boost()    */
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|/**    * Expert: creates a field with no initial value.    * Intended only for custom Field subclasses.    * @param name field name    * @param type field type    * @throws IllegalArgumentException if either the name or type    *         is null.    */
DECL|method|Field
specifier|protected
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Create field with Reader value.    * @param name field name    * @param reader reader value    * @param type field type    * @throws IllegalArgumentException if either the name or type    *         is null, or if the field's type is stored(), or    *         if tokenized() is false.    * @throws NullPointerException if the reader is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"reader must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|stored
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fields with a Reader value cannot be stored"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
operator|!
name|type
operator|.
name|tokenized
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"non-tokenized fields must use String values"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Create field with TokenStream value.    * @param name field name    * @param tokenStream TokenStream value    * @param type field type    * @throws IllegalArgumentException if either the name or type    *         is null, or if the field's type is stored(), or    *         if tokenized() is false, or if indexed() is false.    * @throws NullPointerException if the tokenStream is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tokenStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"tokenStream must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
operator|!
name|type
operator|.
name|tokenized
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TokenStream fields must be indexed and tokenized"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|stored
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TokenStream fields cannot be stored"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|tokenStream
operator|=
name|tokenStream
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Create field with binary value.    *     *<p>NOTE: the provided byte[] is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value byte array pointing to binary content (not copied)    * @param type field type    * @throws IllegalArgumentException if the field name is null,    *         or the field's type is indexed()    * @throws NullPointerException if the type is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create field with binary value.    *     *<p>NOTE: the provided byte[] is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value byte array pointing to binary content (not copied)    * @param offset starting position of the byte array    * @param length valid length of the byte array    * @param type field type    * @throws IllegalArgumentException if the field name is null,    *         or the field's type is indexed()    * @throws NullPointerException if the type is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create field with binary value.    *    *<p>NOTE: the provided BytesRef is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param bytes BytesRef pointing to binary content (not copied)    * @param type field type    * @throws IllegalArgumentException if the field name is null,    *         or the field's type is indexed()    * @throws NullPointerException if the type is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bytes must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldsData
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|// TODO: allow direct construction of int, long, float, double value too..?
comment|/**    * Create field with String value.    * @param name field name    * @param value string value    * @param type field type    * @throws IllegalArgumentException if either the name or value    *         is null, or if the field's type is neither indexed() nor stored(),     *         or if indexed() is false but storeTermVectors() is true.    * @throws NullPointerException if the type is null    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|stored
argument_list|()
operator|&&
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"it doesn't make sense to have a field that "
operator|+
literal|"is neither indexed nor stored"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * The value of the field as a String, or null. If null, the Reader value or    * binary value is used. Exactly one of stringValue(), readerValue(), and    * binaryValue() must be set.    */
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
if|if
condition|(
name|fieldsData
operator|instanceof
name|String
operator|||
name|fieldsData
operator|instanceof
name|Number
condition|)
block|{
return|return
name|fieldsData
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * The value of the field as a Reader, or null. If null, the String value or    * binary value is used. Exactly one of stringValue(), readerValue(), and    * binaryValue() must be set.    */
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|Reader
condition|?
operator|(
name|Reader
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/**    * The TokenStream for this field to be used when indexing, or null. If null,    * the Reader value or String value is analyzed to produce the indexed tokens.    */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
name|tokenStream
return|;
block|}
comment|/**    *<p>    * Expert: change the value of this field. This can be used during indexing to    * re-use a single Field instance to improve indexing speed by avoiding GC    * cost of new'ing and reclaiming Field instances. Typically a single    * {@link Document} instance is re-used as well. This helps most on small    * documents.    *</p>    *     *<p>    * Each Field instance should only be used once within a single    * {@link Document} instance. See<a    * href="http://wiki.apache.org/lucene-java/ImproveIndexingSpeed"    *>ImproveIndexingSpeed</a> for details.    *</p>    */
DECL|method|setStringValue
specifier|public
name|void
name|setStringValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to String"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value must not be null"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setReaderValue
specifier|public
name|void
name|setReaderValue
parameter_list|(
name|Reader
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Reader
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Reader"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    *    *<p>NOTE: the provided BytesRef is not copied so be sure    * not to change it until you're done with this field.    */
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|BytesRef
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to BytesRef"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a BytesRef value on an indexed field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value must not be null"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setByteValue
specifier|public
name|void
name|setByteValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Byte
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Byte"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Byte
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setShortValue
specifier|public
name|void
name|setShortValue
parameter_list|(
name|short
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Short
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Short"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setIntValue
specifier|public
name|void
name|setIntValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Integer
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Integer"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setLongValue
specifier|public
name|void
name|setLongValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Long
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Long"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setFloatValue
specifier|public
name|void
name|setFloatValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Float
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Float"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See     * {@link #setStringValue(String)}.    */
DECL|method|setDoubleValue
specifier|public
name|void
name|setDoubleValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|fieldsData
operator|instanceof
name|Double
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from "
operator|+
name|fieldsData
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to Double"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: sets the token stream to be used for indexing and causes    * isIndexed() and isTokenized() to return true. May be combined with stored    * values from stringValue() or binaryValue()    */
DECL|method|setTokenStream
specifier|public
name|void
name|setTokenStream
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
operator|!
name|type
operator|.
name|tokenized
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TokenStream fields must be indexed and tokenized"
argument_list|)
throw|;
block|}
name|this
operator|.
name|tokenStream
operator|=
name|tokenStream
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**     * {@inheritDoc}    *<p>    * The default value is<code>1.0f</code> (no boost).    * @see #setBoost(float)    */
annotation|@
name|Override
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/**     * Sets the boost factor on this field.    * @throws IllegalArgumentException if this field is not indexed,     *         or if it omits norms.     * @see #boost()    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
name|type
operator|.
name|omitNorms
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You cannot set an index-time boost on an unindexed field, or one that omits norms"
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
if|if
condition|(
name|fieldsData
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
name|Number
operator|)
name|fieldsData
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
if|if
condition|(
name|fieldsData
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
operator|(
name|BytesRef
operator|)
name|fieldsData
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsData
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|fieldsData
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns the {@link FieldType} for this field. */
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|FieldType
name|fieldType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// Not indexed
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldType
argument_list|()
operator|.
name|tokenized
argument_list|()
condition|)
block|{
if|if
condition|(
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|reuse
operator|instanceof
name|StringTokenStream
operator|)
condition|)
block|{
comment|// lazy init the TokenStream as it is heavy to instantiate
comment|// (attributes,...) if not needed
name|reuse
operator|=
operator|new
name|StringTokenStream
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|StringTokenStream
operator|)
name|reuse
operator|)
operator|.
name|setValue
argument_list|(
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
elseif|else
if|if
condition|(
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|reuse
operator|instanceof
name|BinaryTokenStream
operator|)
condition|)
block|{
comment|// lazy init the TokenStream as it is heavy to instantiate
comment|// (attributes,...) if not needed
name|reuse
operator|=
operator|new
name|BinaryTokenStream
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|BinaryTokenStream
operator|)
name|reuse
operator|)
operator|.
name|setValue
argument_list|(
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Non-Tokenized Fields must have a String value"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|tokenStream
operator|!=
literal|null
condition|)
block|{
return|return
name|tokenStream
return|;
block|}
elseif|else
if|if
condition|(
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|name
argument_list|()
argument_list|,
name|readerValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|name
argument_list|()
argument_list|,
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field must have either TokenStream, String, Reader or Number value; got "
operator|+
name|this
argument_list|)
throw|;
block|}
DECL|class|BinaryTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|BinaryTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|bytesAtt
specifier|private
specifier|final
name|BytesTermAttribute
name|bytesAtt
init|=
name|addAttribute
argument_list|(
name|BytesTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|true
decl_stmt|;
DECL|field|value
specifier|private
name|BytesRef
name|value
decl_stmt|;
comment|/** Creates a new TokenStream that returns a BytesRef as single token.      *<p>Warning: Does not initialize the value, you must call      * {@link #setValue(BytesRef)} afterwards!      */
DECL|method|BinaryTokenStream
name|BinaryTokenStream
parameter_list|()
block|{     }
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|used
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|bytesAtt
operator|.
name|setBytesRef
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|used
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|StringTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|StringTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|true
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
init|=
literal|null
decl_stmt|;
comment|/** Creates a new TokenStream that returns a String as single token.      *<p>Warning: Does not initialize the value, you must call      * {@link #setValue(String)} afterwards!      */
DECL|method|StringTokenStream
name|StringTokenStream
parameter_list|()
block|{     }
comment|/** Sets the string value. */
DECL|method|setValue
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|used
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
specifier|final
name|int
name|finalOffset
init|=
name|value
operator|.
name|length
argument_list|()
decl_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|used
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Specifies whether and how a field should be stored. */
DECL|enum|Store
specifier|public
specifier|static
enum|enum
name|Store
block|{
comment|/** Store the original field value in the index. This is useful for short texts      * like a document's title which should be displayed with the results. The      * value is stored in its original form, i.e. no analyzer is used before it is      * stored.      */
DECL|enum constant|YES
name|YES
block|,
comment|/** Do not store the field value in the index. */
DECL|enum constant|NO
name|NO
block|}
block|}
end_class

end_unit

