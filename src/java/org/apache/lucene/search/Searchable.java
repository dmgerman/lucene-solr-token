begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FieldSelector
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
name|index
operator|.
name|Term
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
name|CorruptIndexException
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
comment|// for javadoc
end_comment

begin_comment
comment|/** The interface for search implementations.  *  *<p>Searchable is the abstract network protocol for searching.   * Implementations provide search over a single index, over multiple  * indices, and over indices on remote servers.  *  *<p>Queries, filters and sort criteria are designed to be compact so that  * they may be efficiently passed to a remote index, with only the top-scoring  * hits being returned, rather than every matching hit.  */
end_comment

begin_interface
DECL|interface|Searchable
specifier|public
interface|interface
name|Searchable
block|{
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every non-zero    * scoring document.    *<br>HitCollector-based access to remote indexes is discouraged.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param weight to match documents    * @param filter if non-null, used to permit documents to be collected.    * @param results to receive hits    * @throws BooleanQuery.TooManyClauses    * @deprecated use {@link #search(Weight, Filter, Collector)} instead.    */
DECL|method|search
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lower-level search API.    *     *<p>    * {@link Collector#collect(int)} is called for every document.<br>    * Collector-based access to remote indexes is discouraged.    *     *<p>    * Applications should only use this if they need<i>all</i> of the matching    * documents. The high-level search API ({@link Searcher#search(Query)}) is    * usually more efficient, as it skips non-high-scoring hits.    *     * @param weight    *          to match documents    * @param filter    *          if non-null, used to permit documents to be collected.    * @param collector    *          to receive hits    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Frees resources associated with this Searcher.    * Be careful not to call this method while you are still using objects    * like {@link Hits}.    */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns the number of documents containing<code>term</code>.    * Called by search code to compute term weights.    * @see IndexReader#docFreq(Term)    */
DECL|method|docFreq
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: For each term in the terms array, calculates the number of    * documents containing<code>term</code>. Returns an array with these    * document frequencies. Used to minimize number of remote calls.    */
DECL|method|docFreqs
name|int
index|[]
name|docFreqs
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns one greater than the largest possible document number.    * Called by search code to compute term weights.    * @see IndexReader#maxDoc()    */
DECL|method|maxDoc
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Low-level search implementation.  Finds the top<code>n</code>    * hits for<code>query</code>, applying<code>filter</code> if non-null.    *    *<p>Called by {@link Hits}.    *    *<p>Applications should usually call {@link Searcher#search(Query)} or    * {@link Searcher#search(Query,Filter)} instead.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns the stored fields of document<code>i</code>.    * Called by {@link HitCollector} implementations.    * @see IndexReader#document(int)    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|doc
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/**    * Get the {@link org.apache.lucene.document.Document} at the<code>n</code><sup>th</sup> position. The {@link org.apache.lucene.document.FieldSelector}    * may be used to determine what {@link org.apache.lucene.document.Field}s to load and how they should be loaded.    *     *<b>NOTE:</b> If the underlying Reader (more specifically, the underlying<code>FieldsReader</code>) is closed before the lazy {@link org.apache.lucene.document.Field} is    * loaded an exception may be thrown.  If you want the value of a lazy {@link org.apache.lucene.document.Field} to be available after closing you must    * explicitly load it or fetch the Document again with a new loader.    *     *      * @param n Get the document at the<code>n</code><sup>th</sup> position    * @param fieldSelector The {@link org.apache.lucene.document.FieldSelector} to use to determine what Fields should be loaded on the Document.  May be null, in which case all Fields will be loaded.    * @return The stored fields of the {@link org.apache.lucene.document.Document} at the nth position    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    *     * @see IndexReader#document(int, FieldSelector)    * @see org.apache.lucene.document.Fieldable    * @see org.apache.lucene.document.FieldSelector    * @see org.apache.lucene.document.SetBasedFieldSelector    * @see org.apache.lucene.document.LoadFirstFieldSelector    */
DECL|method|doc
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/** Expert: called to re-write queries into primitive queries.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|rewrite
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: low-level implementation method    * Returns an Explanation that describes how<code>doc</code> scored against    *<code>weight</code>.    *    *<p>This is intended to be used in developing Similarity implementations,    * and, for good performance, should not be displayed with every hit.    * Computing an explanation is as expensive as executing the query over the    * entire index.    *<p>Applications should call {@link Searcher#explain(Query, int)}.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|explain
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// TODO: change the javadoc in 3.0 to remove the last NOTE section.
comment|/** Expert: Low-level search implementation with arbitrary sorting.  Finds    * the top<code>n</code> hits for<code>query</code>, applying    *<code>filter</code> if non-null, and sorting the hits by the criteria in    *<code>sort</code>.    *    *<p>Applications should usually call {@link    * Searcher#search(Query,Filter,Sort)} instead.    *     *<b>NOTE:</b> currently, this method tracks document scores and sets them in    * the returned {@link FieldDoc}, however in 3.0 it will move to not track    * document scores. If document scores tracking is still needed, you can use    * {@link #search(Weight, Filter, Collector)} and pass in a    * {@link TopFieldCollector} instance.    *     * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

