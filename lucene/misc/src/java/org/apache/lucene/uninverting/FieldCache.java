begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.uninverting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|uninverting
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
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|NumericTokenStream
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
name|document
operator|.
name|NumericDocValuesField
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
name|LeafReader
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
name|BinaryDocValues
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
comment|// javadocs
end_comment

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
name|NumericDocValues
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|Terms
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
name|TermsEnum
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
name|Accountable
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
name|Bits
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
name|NumericUtils
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Expert: Maintains caches of term values.  *  *<p>Created: May 19, 2004 11:13:14 AM  *  * @since   lucene 1.4  * @see FieldCacheSanityChecker  *  * @lucene.internal  */
end_comment

begin_interface
DECL|interface|FieldCache
interface|interface
name|FieldCache
block|{
comment|/**    * Placeholder indicating creation of this cache is currently in-progress.    */
DECL|class|CreationPlaceholder
specifier|public
specifier|static
specifier|final
class|class
name|CreationPlaceholder
implements|implements
name|Accountable
block|{
DECL|field|value
name|Accountable
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// don't call on the in-progress value, might make things angry.
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
return|;
block|}
block|}
comment|/**    * interface to all parsers. It is used to parse different numeric types.    */
DECL|interface|Parser
specifier|public
interface|interface
name|Parser
block|{
comment|/**      * Pulls a {@link TermsEnum} from the given {@link Terms}. This method allows certain parsers      * to filter the actual TermsEnum before the field cache is filled.      *       * @param terms the {@link Terms} instance to create the {@link TermsEnum} from.      * @return a possibly filtered {@link TermsEnum} instance, this method must not return<code>null</code>.      * @throws IOException if an {@link IOException} occurs      */
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Parse's this field's value */
DECL|method|parseValue
specifier|public
name|long
name|parseValue
parameter_list|(
name|BytesRef
name|term
parameter_list|)
function_decl|;
block|}
comment|/** Expert: The cache used internally by sorting and range query classes. */
DECL|field|DEFAULT
specifier|public
specifier|static
name|FieldCache
name|DEFAULT
init|=
operator|new
name|FieldCacheImpl
argument_list|()
decl_stmt|;
comment|/**    * A parser instance for int values encoded by {@link NumericUtils}, e.g. when indexed    * via {@link IntField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_INT_PARSER
specifier|public
specifier|static
specifier|final
name|Parser
name|NUMERIC_UTILS_INT_PARSER
init|=
operator|new
name|Parser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|parseValue
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NumericUtils
operator|.
name|filterPrefixCodedInts
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_INT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for float values encoded with {@link NumericUtils}, e.g. when indexed    * via {@link FloatField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_FLOAT_PARSER
specifier|public
specifier|static
specifier|final
name|Parser
name|NUMERIC_UTILS_FLOAT_PARSER
init|=
operator|new
name|Parser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|parseValue
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|int
name|val
init|=
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|val
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_FLOAT_PARSER"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NumericUtils
operator|.
name|filterPrefixCodedInts
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for long values encoded by {@link NumericUtils}, e.g. when indexed    * via {@link LongField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_LONG_PARSER
specifier|public
specifier|static
specifier|final
name|Parser
name|NUMERIC_UTILS_LONG_PARSER
init|=
operator|new
name|Parser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|parseValue
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_LONG_PARSER"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NumericUtils
operator|.
name|filterPrefixCodedLongs
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for double values encoded with {@link NumericUtils}, e.g. when indexed    * via {@link DoubleField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_DOUBLE_PARSER
specifier|public
specifier|static
specifier|final
name|Parser
name|NUMERIC_UTILS_DOUBLE_PARSER
init|=
operator|new
name|Parser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|parseValue
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|long
name|val
init|=
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|val
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_DOUBLE_PARSER"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NumericUtils
operator|.
name|filterPrefixCodedLongs
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Checks the internal cache for an appropriate entry, and if none is found,    *  reads the terms in<code>field</code> and returns a bit set at the size of    *<code>reader.maxDoc()</code>, with turned on bits for each docid that     *  does have a value for this field.    */
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a {@link NumericDocValues} over the values found in documents in the given    * field. If the field was indexed as {@link NumericDocValuesField}, it simply    * uses {@link org.apache.lucene.index.LeafReader#getNumericDocValues(String)} to read the values.    * Otherwise, it checks the internal cache for an appropriate entry, and if    * none is found, reads the terms in<code>field</code> as longs and returns    * an array of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    *     * @param reader    *          Used to get field values.    * @param field    *          Which field contains the longs.    * @param parser    *          Computes long for string values. May be {@code null} if the    *          requested field was indexed as {@link NumericDocValuesField} or    *          {@link LongField}.    * @param setDocsWithField    *          If true then {@link #getDocsWithField} will also be computed and    *          stored in the FieldCache.    * @return The values in the given field for each document.    * @throws IOException    *           If any error occurs.    */
DECL|method|getNumerics
specifier|public
name|NumericDocValues
name|getNumerics
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|boolean
name|setDocsWithField
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found, reads the term values in<code>field</code>    * and returns a {@link BinaryDocValues} instance, providing a    * method to retrieve the term (as a BytesRef) per document.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @param setDocsWithField  If true then {@link #getDocsWithField} will    *        also be computed and stored in the FieldCache.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getTerms
specifier|public
name|BinaryDocValues
name|getTerms
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|setDocsWithField
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: just like {@link #getTerms(org.apache.lucene.index.LeafReader,String,boolean)},    *  but you can specify whether more RAM should be consumed in exchange for    *  faster lookups (default is "true").  Note that the    *  first call for a given reader and field "wins",    *  subsequent calls will share the same cache entry. */
DECL|method|getTerms
specifier|public
name|BinaryDocValues
name|getTerms
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|setDocsWithField
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found, reads the term values in<code>field</code>    * and returns a {@link SortedDocValues} instance,    * providing methods to retrieve sort ordinals and terms    * (as a ByteRef) per document.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getTermsIndex
specifier|public
name|SortedDocValues
name|getTermsIndex
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: just like {@link    *  #getTermsIndex(org.apache.lucene.index.LeafReader,String)}, but you can specify    *  whether more RAM should be consumed in exchange for    *  faster lookups (default is "true").  Note that the    *  first call for a given reader and field "wins",    *  subsequent calls will share the same cache entry. */
DECL|method|getTermsIndex
specifier|public
name|SortedDocValues
name|getTermsIndex
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Can be passed to {@link #getDocTermOrds} to filter for 32-bit numeric terms */
DECL|field|INT32_TERM_PREFIX
specifier|public
specifier|static
specifier|final
name|BytesRef
name|INT32_TERM_PREFIX
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
name|NumericUtils
operator|.
name|SHIFT_START_INT
block|}
argument_list|)
decl_stmt|;
comment|/** Can be passed to {@link #getDocTermOrds} to filter for 64-bit numeric terms */
DECL|field|INT64_TERM_PREFIX
specifier|public
specifier|static
specifier|final
name|BytesRef
name|INT64_TERM_PREFIX
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
name|NumericUtils
operator|.
name|SHIFT_START_LONG
block|}
argument_list|)
decl_stmt|;
comment|/**    * Checks the internal cache for an appropriate entry, and if none is found, reads the term values    * in<code>field</code> and returns a {@link DocTermOrds} instance, providing a method to retrieve    * the terms (as ords) per document.    *    * @param reader  Used to build a {@link DocTermOrds} instance    * @param field   Which field contains the strings.    * @param prefix  prefix for a subset of the terms which should be uninverted. Can be null or    *                {@link #INT32_TERM_PREFIX} or {@link #INT64_TERM_PREFIX}    *                    * @return a {@link DocTermOrds} instance    * @throws IOException  If any error occurs.    */
DECL|method|getDocTermOrds
specifier|public
name|SortedSetDocValues
name|getDocTermOrds
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * EXPERT: A unique Identifier/Description for each item in the FieldCache.     * Can be useful for logging/debugging.    * @lucene.experimental    */
DECL|class|CacheEntry
specifier|public
specifier|final
class|class
name|CacheEntry
block|{
DECL|field|readerKey
specifier|private
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|cacheType
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cacheType
decl_stmt|;
DECL|field|custom
specifier|private
specifier|final
name|Object
name|custom
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Accountable
name|value
decl_stmt|;
DECL|method|CacheEntry
specifier|public
name|CacheEntry
parameter_list|(
name|Object
name|readerKey
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|cacheType
parameter_list|,
name|Object
name|custom
parameter_list|,
name|Accountable
name|value
parameter_list|)
block|{
name|this
operator|.
name|readerKey
operator|=
name|readerKey
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|cacheType
operator|=
name|cacheType
expr_stmt|;
name|this
operator|.
name|custom
operator|=
name|custom
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getReaderKey
specifier|public
name|Object
name|getReaderKey
parameter_list|()
block|{
return|return
name|readerKey
return|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|getCacheType
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getCacheType
parameter_list|()
block|{
return|return
name|cacheType
return|;
block|}
DECL|method|getCustom
specifier|public
name|Object
name|getCustom
parameter_list|()
block|{
return|return
name|custom
return|;
block|}
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * The most recently estimated size of the value, null unless       * estimateSize has been called.      */
DECL|method|getEstimatedSize
specifier|public
name|String
name|getEstimatedSize
parameter_list|()
block|{
name|long
name|bytesUsed
init|=
name|value
operator|==
literal|null
condition|?
literal|0L
else|:
name|value
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
return|return
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|bytesUsed
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
operator|.
name|append
argument_list|(
name|getReaderKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'=>"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
operator|.
name|append
argument_list|(
name|getFieldName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"',"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|getCacheType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|getCustom
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"=>"
argument_list|)
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|System
operator|.
name|identityHashCode
argument_list|(
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|getEstimatedSize
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" (size =~ "
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * EXPERT: Generates an array of CacheEntry objects representing all items     * currently in the FieldCache.    *<p>    * NOTE: These CacheEntry objects maintain a strong reference to the     * Cached Values.  Maintaining references to a CacheEntry the AtomicIndexReader     * associated with it has garbage collected will prevent the Value itself    * from being garbage collected when the Cache drops the WeakReference.    *</p>    * @lucene.experimental    */
DECL|method|getCacheEntries
specifier|public
name|CacheEntry
index|[]
name|getCacheEntries
parameter_list|()
function_decl|;
comment|/**    *<p>    * EXPERT: Instructs the FieldCache to forcibly expunge all entries     * from the underlying caches.  This is intended only to be used for     * test methods as a way to ensure a known base state of the Cache     * (with out needing to rely on GC to free WeakReferences).      * It should not be relied on for "Cache maintenance" in general     * application code.    *</p>    * @lucene.experimental    */
DECL|method|purgeAllCaches
specifier|public
name|void
name|purgeAllCaches
parameter_list|()
function_decl|;
comment|/**    * Expert: drops all cache entries associated with this    * reader {@link IndexReader#getCoreCacheKey}.  NOTE: this cache key must    * precisely match the reader that the cache entry is    * keyed on. If you pass a top-level reader, it usually    * will have no effect as Lucene now caches at the segment    * reader level.    */
DECL|method|purgeByCacheKey
specifier|public
name|void
name|purgeByCacheKey
parameter_list|(
name|Object
name|coreCacheKey
parameter_list|)
function_decl|;
comment|/**    * If non-null, FieldCacheImpl will warn whenever    * entries are created that are not sane according to    * {@link FieldCacheSanityChecker}.    */
DECL|method|setInfoStream
specifier|public
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|stream
parameter_list|)
function_decl|;
comment|/** counterpart of {@link #setInfoStream(PrintStream)} */
DECL|method|getInfoStream
specifier|public
name|PrintStream
name|getInfoStream
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

