begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Date
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
name|IndexReader
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

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
name|Similarity
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

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
name|Hits
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

begin_comment
comment|/**   A field is a section of a Document.  Each field has two parts, a name and a   value.  Values may be free text, provided as a String or as a Reader, or they   may be atomic keywords, which are not further processed.  Such keywords may   be used to represent dates, urls, etc.  Fields are optionally stored in the   index, so that they may be returned with hits on the document.   */
end_comment

begin_class
DECL|class|Field
specifier|public
specifier|final
class|class
name|Field
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|"body"
decl_stmt|;
DECL|field|stringValue
specifier|private
name|String
name|stringValue
init|=
literal|null
decl_stmt|;
DECL|field|storeTermVector
specifier|private
name|boolean
name|storeTermVector
init|=
literal|false
decl_stmt|;
DECL|field|readerValue
specifier|private
name|Reader
name|readerValue
init|=
literal|null
decl_stmt|;
DECL|field|isStored
specifier|private
name|boolean
name|isStored
init|=
literal|false
decl_stmt|;
DECL|field|isIndexed
specifier|private
name|boolean
name|isIndexed
init|=
literal|true
decl_stmt|;
DECL|field|isTokenized
specifier|private
name|boolean
name|isTokenized
init|=
literal|true
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
DECL|class|Store
specifier|public
specifier|static
specifier|final
class|class
name|Store
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|Store
specifier|private
name|Store
parameter_list|()
block|{}
DECL|method|Store
specifier|private
name|Store
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Store the original field value in the index. This is useful for short texts      * like a document's title which should be displayed with the results. The      * value is stored in its original form, i.e. no analyzer is used before it is      * stored.       */
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|Store
name|YES
init|=
operator|new
name|Store
argument_list|(
literal|"YES"
argument_list|)
decl_stmt|;
comment|/** Do not store the field value in the index. */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|Store
name|NO
init|=
operator|new
name|Store
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
block|}
DECL|class|Index
specifier|public
specifier|static
specifier|final
class|class
name|Index
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|Index
specifier|private
name|Index
parameter_list|()
block|{}
DECL|method|Index
specifier|private
name|Index
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Do not index the field value. This field can thus not be searched,      * but one can still access its contents provided it is       * {@link Field.Store stored}. */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|Index
name|NO
init|=
operator|new
name|Index
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
comment|/** Index the field's value so it can be searched. An Analyzer will be used      * to tokenize and possibly further normalize the text before its      * terms will be stored in the index. This is useful for common text.      */
DECL|field|TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|TOKENIZED
init|=
operator|new
name|Index
argument_list|(
literal|"TOKENIZED"
argument_list|)
decl_stmt|;
comment|/** Index the field's value without using an Analyzer, so it can be searched.      * As no analyzer is used the value will be stored as a single term. This is       * useful for unique Ids like product numbers.      */
DECL|field|UN_TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|UN_TOKENIZED
init|=
operator|new
name|Index
argument_list|(
literal|"UN_TOKENIZED"
argument_list|)
decl_stmt|;
block|}
DECL|class|TermVector
specifier|public
specifier|static
specifier|final
class|class
name|TermVector
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|TermVector
specifier|private
name|TermVector
parameter_list|()
block|{}
DECL|method|TermVector
specifier|private
name|TermVector
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Do not store term vectors.       */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|TermVector
name|NO
init|=
operator|new
name|TermVector
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
comment|/** Store the term vectors of each document. A term vector is a list      * of the document's terms and their number of occurences in that document. */
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|TermVector
name|YES
init|=
operator|new
name|TermVector
argument_list|(
literal|"YES"
argument_list|)
decl_stmt|;
block|}
comment|/** Sets the boost factor hits on this field.  This value will be    * multiplied into the score of all hits on this this field of this    * document.    *    *<p>The boost is multiplied by {@link Document#getBoost()} of the document    * containing this field.  If a document has multiple fields with the same    * name, all such values are multiplied together.  This product is then    * multipled by the value {@link Similarity#lengthNorm(String,int)}, and    * rounded by {@link Similarity#encodeNorm(float)} before it is stored in the    * index.  One should attempt to ensure that this product does not overflow    * the range of that encoding.    *    * @see Document#setBoost(float)    * @see Similarity#lengthNorm(String, int)    * @see Similarity#encodeNorm(float)    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/** Returns the boost factor for hits for this field.    *    *<p>The default value is 1.0.    *    *<p>Note: this value is not stored directly with the document in the index.    * Documents returned from {@link IndexReader#document(int)} and    * {@link Hits#doc(int)} may thus not have the same value present as when    * this field was indexed.    *    * @see #setBoost(float)    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Constructs a String-valued Field that is not tokenized, but is indexed     and stored.  Useful for non-text fields, e.g. date or url.       @deprecated use {@link #Field(String, String, Field.Store, Field.Index)       Field(name, value, Field.Store.YES, Field.Index.UN_TOKENIZED)} instead */
DECL|method|Keyword
specifier|public
specifier|static
specifier|final
name|Field
name|Keyword
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is not tokenized nor indexed,     but is stored in the index, for return with hits.     @deprecated use {@link #Field(String, String, Field.Store, Field.Index)       Field(name, value, Field.Store.YES, Field.Index.NO)} instead */
DECL|method|UnIndexed
specifier|public
specifier|static
specifier|final
name|Field
name|UnIndexed
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     and is stored in the index, for return with hits.  Useful for short text     fields, like "title" or "subject". Term vector will not be stored for this field.   @deprecated use {@link #Field(String, String, Field.Store, Field.Index)     Field(name, value, Field.Store.YES, Field.Index.TOKENIZED)} instead */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|Text
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a Date-valued Field that is not tokenized and is indexed,       and stored in the index, for return with hits.       @deprecated use {@link #Field(String, String, Field.Store, Field.Index)       Field(name, value, Field.Store.YES, Field.Index.UN_TOKENIZED)} instead */
DECL|method|Keyword
specifier|public
specifier|static
specifier|final
name|Field
name|Keyword
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|value
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|DateField
operator|.
name|dateToString
argument_list|(
name|value
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     and is stored in the index, for return with hits.  Useful for short text     fields, like "title" or "subject".     @deprecated use {@link #Field(String, String, Field.Store, Field.Index, Field.TermVector)       Field(name, value, Field.Store.YES, Field.Index.TOKENIZED, storeTermVector)} instead */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|storeTermVector
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     but that is not stored in the index.  Term vector will not be stored for this field.     @deprecated use {@link #Field(String, String, Field.Store, Field.Index)       Field(name, value, Field.Store.NO, Field.Index.TOKENIZED)} instead */
DECL|method|UnStored
specifier|public
specifier|static
specifier|final
name|Field
name|UnStored
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|UnStored
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a String-valued Field that is tokenized and indexed,     but that is not stored in the index.     @deprecated use {@link #Field(String, String, Field.Store, Field.Index, Field.TermVector)       Field(name, value, Field.Store.NO, Field.Index.TOKENIZED, storeTermVector)} instead */
DECL|method|UnStored
specifier|public
specifier|static
specifier|final
name|Field
name|UnStored
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|storeTermVector
argument_list|)
return|;
block|}
comment|/** Constructs a Reader-valued Field that is tokenized and indexed, but is     not stored in the index verbatim.  Useful for longer text fields, like     "body". Term vector will not be stored for this field.     @deprecated use {@link #Field(String, Reader) Field(name, value)} instead */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|value
parameter_list|)
block|{
return|return
name|Text
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Constructs a Reader-valued Field that is tokenized and indexed, but is     not stored in the index verbatim.  Useful for longer text fields, like     "body".     @deprecated use {@link #Field(String, Reader, Field.TermVector)       Field(name, value, storeTermVector)} instead */
DECL|method|Text
specifier|public
specifier|static
specifier|final
name|Field
name|Text
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|value
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|f
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/** The name of the field (e.g., "date", "title", "body", ...)     as an interned string. */
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
comment|/** The value of the field as a String, or null.  If null, the Reader value     is used.  Exactly one of stringValue() and readerValue() must be set. */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|stringValue
return|;
block|}
comment|/** The value of the field as a Reader, or null.  If null, the String value     is used.  Exactly one of stringValue() and readerValue() must be set. */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
name|readerValue
return|;
block|}
comment|/**    * Create a field by specifying its name, value and how it will    * be saved in the index. Term vectors will not be stored in the index.    *     * @param name The name of the field    * @param value The string to process    * @param store Whether<code>value</code> should be stored in the index    * @param index Whether the field should be indexed, and if so, if it should    *  be tokenized before indexing     * @throws NullPointerException if name or value is<code>null</code>    * @throws IllegalArgumentException if the field is neither stored nor indexed     */
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
name|Store
name|store
parameter_list|,
name|Index
name|index
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a field by specifying its name, value and how it will    * be saved in the index.    *     * @param name The name of the field    * @param value The string to process    * @param store Whether<code>value</code> should be stored in the index    * @param index Whether the field should be indexed, and if so, if it should    *  be tokenized before indexing     * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or value is<code>null</code>    * @throws IllegalArgumentException in any of the following situations:    *<ul>     *<li>the field is neither stored nor indexed</li>     *<li>the field is not indexed but termVector is<code>TermVector.YES</code></li>    *</ul>     */
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
name|Store
name|store
parameter_list|,
name|Index
name|index
parameter_list|,
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
operator|&&
name|store
operator|==
name|Store
operator|.
name|NO
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"it doesn't make sense to have a field that "
operator|+
literal|"is neither indexed nor stored"
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
operator|&&
name|termVector
operator|!=
name|TermVector
operator|.
name|NO
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store term vector information "
operator|+
literal|"for a field that is not indexed"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|stringValue
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|YES
condition|)
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
elseif|else
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|NO
condition|)
name|this
operator|.
name|isStored
operator|=
literal|false
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown store parameter "
operator|+
name|store
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|TOKENIZED
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|UN_TOKENIZED
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown index parameter "
operator|+
name|index
argument_list|)
throw|;
block|}
name|setStoreTermVector
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a tokenized and indexed field that is not stored. Term vectors will    * not be stored.    *     * @param name The name of the field    * @param reader The reader with the content    * @throws NullPointerException if name or reader is<code>null</code>    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|reader
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a tokenized and indexed field that is not stored, optionally with     * storing term vectors.    *     * @param name The name of the field    * @param reader The reader with the content    * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or reader is<code>null</code>    */
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
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"reader cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|readerValue
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|isStored
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|true
expr_stmt|;
name|setStoreTermVector
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/** Create a field by specifying all parameters except for<code>storeTermVector</code>,    *  which is set to<code>false</code>.    *     * @deprecated use {@link #Field(String, String, Field.Store, Field.Index)} instead    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|string
parameter_list|,
name|boolean
name|store
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|token
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|string
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|token
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @param name The name of the field    * @param string The string to process    * @param store true if the field should store the string    * @param index true if the field should be indexed    * @param token true if the field should be tokenized    * @param storeTermVector true if we should store the Term Vector info    *     * @deprecated use {@link #Field(String, String, Field.Store, Field.Index, Field.TermVector)} instead    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|string
parameter_list|,
name|boolean
name|store
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|token
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|string
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|index
operator|&&
name|storeTermVector
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store a term vector for fields that are not indexed"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|stringValue
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|isStored
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
block|}
DECL|method|setStoreTermVector
specifier|private
name|void
name|setStoreTermVector
parameter_list|(
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|termVector
operator|==
name|TermVector
operator|.
name|NO
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termVector
operator|==
name|TermVector
operator|.
name|YES
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown termVector parameter "
operator|+
name|termVector
argument_list|)
throw|;
block|}
block|}
comment|/** True iff the value of the field is to be stored in the index for return     with search hits.  It is an error for this to be true if a field is     Reader-valued. */
DECL|method|isStored
specifier|public
specifier|final
name|boolean
name|isStored
parameter_list|()
block|{
return|return
name|isStored
return|;
block|}
comment|/** True iff the value of the field is to be indexed, so that it may be     searched on. */
DECL|method|isIndexed
specifier|public
specifier|final
name|boolean
name|isIndexed
parameter_list|()
block|{
return|return
name|isIndexed
return|;
block|}
comment|/** True iff the value of the field should be tokenized as text prior to     indexing.  Un-tokenized fields are indexed as a single word and may not be     Reader-valued. */
DECL|method|isTokenized
specifier|public
specifier|final
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
name|isTokenized
return|;
block|}
comment|/** True iff the term or terms used to index this field are stored as a term    *  vector, available from {@link IndexReader#getTermFreqVector(int,String)}.    *  These methods do not provide access to the original content of the field,    *  only to terms used to index it. If the original content must be    *  preserved, use the<code>stored</code> attribute instead.    *    * @see IndexReader#getTermFreqVector(int, String)    */
DECL|method|isTermVectorStored
specifier|public
specifier|final
name|boolean
name|isTermVectorStored
parameter_list|()
block|{
return|return
name|storeTermVector
return|;
block|}
comment|/** Prints a Field for human consumption. */
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|isStored
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"stored"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isIndexed
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"indexed"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isTokenized
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"tokenized"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVector
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVector"
argument_list|)
expr_stmt|;
block|}
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
name|readerValue
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|readerValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
name|stringValue
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
block|}
end_class

end_unit

