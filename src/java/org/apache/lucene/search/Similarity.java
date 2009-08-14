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
name|index
operator|.
name|FieldInvertState
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
name|util
operator|.
name|SmallFloat
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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/** Expert: Scoring API.  *<p>Subclasses implement search scoring.  *  *<p>The score of query<code>q</code> for document<code>d</code> correlates to the  * cosine-distance or dot-product between document and query vectors in a  *<a href="http://en.wikipedia.org/wiki/Vector_Space_Model">  * Vector Space Model (VSM) of Information Retrieval</a>.  * A document whose vector is closer to the query vector in that model is scored higher.  *  * The score is computed as follows:  *  *<P>  *<table cellpadding="1" cellspacing="0" border="1" align="center">  *<tr><td>  *<table cellpadding="1" cellspacing="0" border="0" align="center">  *<tr>  *<td valign="middle" align="right" rowspan="1">  *      score(q,d)&nbsp; =&nbsp;  *<A HREF="#formula_coord">coord(q,d)</A>&nbsp;&middot;&nbsp;  *<A HREF="#formula_queryNorm">queryNorm(q)</A>&nbsp;&middot;&nbsp;  *</td>  *<td valign="bottom" align="center" rowspan="1">  *<big><big><big>&sum;</big></big></big>  *</td>  *<td valign="middle" align="right" rowspan="1">  *<big><big>(</big></big>  *<A HREF="#formula_tf">tf(t in d)</A>&nbsp;&middot;&nbsp;  *<A HREF="#formula_idf">idf(t)</A><sup>2</sup>&nbsp;&middot;&nbsp;  *<A HREF="#formula_termBoost">t.getBoost()</A>&nbsp;&middot;&nbsp;  *<A HREF="#formula_norm">norm(t,d)</A>  *<big><big>)</big></big>  *</td>  *</tr>  *<tr valigh="top">  *<td></td>  *<td align="center"><small>t in q</small></td>  *<td></td>  *</tr>  *</table>  *</td></tr>  *</table>  *  *<p> where  *<ol>  *<li>  *<A NAME="formula_tf"></A>  *<b>tf(t in d)</b>  *      correlates to the term's<i>frequency</i>,  *      defined as the number of times term<i>t</i> appears in the currently scored document<i>d</i>.  *      Documents that have more occurrences of a given term receive a higher score.  *      The default computation for<i>tf(t in d)</i> in  *      {@link org.apache.lucene.search.DefaultSimilarity#tf(float) DefaultSimilarity} is:  *  *<br>&nbsp;<br>  *<table cellpadding="2" cellspacing="2" border="0" align="center">  *<tr>  *<td valign="middle" align="right" rowspan="1">  *            {@link org.apache.lucene.search.DefaultSimilarity#tf(float) tf(t in d)}&nbsp; =&nbsp;  *</td>  *<td valign="top" align="center" rowspan="1">  *               frequency<sup><big>&frac12;</big></sup>  *</td>  *</tr>  *</table>  *<br>&nbsp;<br>  *</li>  *  *<li>  *<A NAME="formula_idf"></A>  *<b>idf(t)</b> stands for Inverse Document Frequency. This value  *      correlates to the inverse of<i>docFreq</i>  *      (the number of documents in which the term<i>t</i> appears).  *      This means rarer terms give higher contribution to the total score.  *      The default computation for<i>idf(t)</i> in  *      {@link org.apache.lucene.search.DefaultSimilarity#idf(int, int) DefaultSimilarity} is:  *  *<br>&nbsp;<br>  *<table cellpadding="2" cellspacing="2" border="0" align="center">  *<tr>  *<td valign="middle" align="right">  *            {@link org.apache.lucene.search.DefaultSimilarity#idf(int, int) idf(t)}&nbsp; =&nbsp;  *</td>  *<td valign="middle" align="center">  *            1 + log<big>(</big>  *</td>  *<td valign="middle" align="center">  *<table>  *<tr><td align="center"><small>numDocs</small></td></tr>  *<tr><td align="center">&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;</td></tr>  *<tr><td align="center"><small>docFreq+1</small></td></tr>  *</table>  *</td>  *<td valign="middle" align="center">  *<big>)</big>  *</td>  *</tr>  *</table>  *<br>&nbsp;<br>  *</li>  *  *<li>  *<A NAME="formula_coord"></A>  *<b>coord(q,d)</b>  *      is a score factor based on how many of the query terms are found in the specified document.  *      Typically, a document that contains more of the query's terms will receive a higher score  *      than another document with fewer query terms.  *      This is a search time factor computed in  *      {@link #coord(int, int) coord(q,d)}  *      by the Similarity in effect at search time.  *<br>&nbsp;<br>  *</li>  *  *<li><b>  *<A NAME="formula_queryNorm"></A>  *      queryNorm(q)  *</b>  *      is a normalizing factor used to make scores between queries comparable.  *      This factor does not affect document ranking (since all ranked documents are multiplied by the same factor),  *      but rather just attempts to make scores from different queries (or even different indexes) comparable.  *      This is a search time factor computed by the Similarity in effect at search time.  *  *      The default computation in  *      {@link org.apache.lucene.search.DefaultSimilarity#queryNorm(float) DefaultSimilarity}  *      is:  *<br>&nbsp;<br>  *<table cellpadding="1" cellspacing="0" border="0" align="center">  *<tr>  *<td valign="middle" align="right" rowspan="1">  *            queryNorm(q)&nbsp; =&nbsp;  *            {@link org.apache.lucene.search.DefaultSimilarity#queryNorm(float) queryNorm(sumOfSquaredWeights)}  *&nbsp; =&nbsp;  *</td>  *<td valign="middle" align="center" rowspan="1">  *<table>  *<tr><td align="center"><big>1</big></td></tr>  *<tr><td align="center"><big>  *&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;&ndash;  *</big></td></tr>  *<tr><td align="center">sumOfSquaredWeights<sup><big>&frac12;</big></sup></td></tr>  *</table>  *</td>  *</tr>  *</table>  *<br>&nbsp;<br>  *  *      The sum of squared weights (of the query terms) is  *      computed by the query {@link org.apache.lucene.search.Weight} object.  *      For example, a {@link org.apache.lucene.search.BooleanQuery boolean query}  *      computes this value as:  *  *<br>&nbsp;<br>  *<table cellpadding="1" cellspacing="0" border="0"n align="center">  *<tr>  *<td valign="middle" align="right" rowspan="1">  *            {@link org.apache.lucene.search.Weight#sumOfSquaredWeights() sumOfSquaredWeights}&nbsp; =&nbsp;  *            {@link org.apache.lucene.search.Query#getBoost() q.getBoost()}<sup><big>2</big></sup>  *&nbsp;&middot;&nbsp;  *</td>  *<td valign="bottom" align="center" rowspan="1">  *<big><big><big>&sum;</big></big></big>  *</td>  *<td valign="middle" align="right" rowspan="1">  *<big><big>(</big></big>  *<A HREF="#formula_idf">idf(t)</A>&nbsp;&middot;&nbsp;  *<A HREF="#formula_termBoost">t.getBoost()</A>  *<big><big>)<sup>2</sup></big></big>  *</td>  *</tr>  *<tr valigh="top">  *<td></td>  *<td align="center"><small>t in q</small></td>  *<td></td>  *</tr>  *</table>  *<br>&nbsp;<br>  *  *</li>  *  *<li>  *<A NAME="formula_termBoost"></A>  *<b>t.getBoost()</b>  *      is a search time boost of term<i>t</i> in the query<i>q</i> as  *      specified in the query text  *      (see<A HREF="../../../../../../queryparsersyntax.html#Boosting a Term">query syntax</A>),  *      or as set by application calls to  *      {@link org.apache.lucene.search.Query#setBoost(float) setBoost()}.  *      Notice that there is really no direct API for accessing a boost of one term in a multi term query,  *      but rather multi terms are represented in a query as multi  *      {@link org.apache.lucene.search.TermQuery TermQuery} objects,  *      and so the boost of a term in the query is accessible by calling the sub-query  *      {@link org.apache.lucene.search.Query#getBoost() getBoost()}.  *<br>&nbsp;<br>  *</li>  *  *<li>  *<A NAME="formula_norm"></A>  *<b>norm(t,d)</b> encapsulates a few (indexing time) boost and length factors:  *  *<ul>  *<li><b>Document boost</b> - set by calling  *        {@link org.apache.lucene.document.Document#setBoost(float) doc.setBoost()}  *        before adding the document to the index.  *</li>  *<li><b>Field boost</b> - set by calling  *        {@link org.apache.lucene.document.Fieldable#setBoost(float) field.setBoost()}  *        before adding the field to a document.  *</li>  *<li>{@link #lengthNorm(String, int)<b>lengthNorm</b>(field)} - computed  *        when the document is added to the index in accordance with the number of tokens  *        of this field in the document, so that shorter fields contribute more to the score.  *        LengthNorm is computed by the Similarity class in effect at indexing.  *</li>  *</ul>  *  *<p>  *      When a document is added to the index, all the above factors are multiplied.  *      If the document has multiple fields with the same name, all their boosts are multiplied together:  *  *<br>&nbsp;<br>  *<table cellpadding="1" cellspacing="0" border="0"n align="center">  *<tr>  *<td valign="middle" align="right" rowspan="1">  *            norm(t,d)&nbsp; =&nbsp;  *            {@link org.apache.lucene.document.Document#getBoost() doc.getBoost()}  *&nbsp;&middot;&nbsp;  *            {@link #lengthNorm(String, int) lengthNorm(field)}  *&nbsp;&middot;&nbsp;  *</td>  *<td valign="bottom" align="center" rowspan="1">  *<big><big><big>&prod;</big></big></big>  *</td>  *<td valign="middle" align="right" rowspan="1">  *            {@link org.apache.lucene.document.Fieldable#getBoost() f.getBoost}()  *</td>  *</tr>  *<tr valigh="top">  *<td></td>  *<td align="center"><small>field<i><b>f</b></i> in<i>d</i> named as<i><b>t</b></i></small></td>  *<td></td>  *</tr>  *</table>  *<br>&nbsp;<br>  *      However the resulted<i>norm</i> value is {@link #encodeNorm(float) encoded} as a single byte  *      before being stored.  *      At search time, the norm byte value is read from the index  *      {@link org.apache.lucene.store.Directory directory} and  *      {@link #decodeNorm(byte) decoded} back to a float<i>norm</i> value.  *      This encoding/decoding, while reducing index size, comes with the price of  *      precision loss - it is not guaranteed that decode(encode(x)) = x.  *      For instance, decode(encode(0.89)) = 0.75.  *      Also notice that search time is too late to modify this<i>norm</i> part of scoring, e.g. by  *      using a different {@link Similarity} for search.  *<br>&nbsp;<br>  *</li>  *</ol>  *  * @see #setDefault(Similarity)  * @see org.apache.lucene.index.IndexWriter#setSimilarity(Similarity)  * @see Searcher#setSimilarity(Similarity)  */
end_comment

begin_class
DECL|class|Similarity
specifier|public
specifier|abstract
class|class
name|Similarity
implements|implements
name|Serializable
block|{
comment|/** The Similarity implementation used by default. */
DECL|field|defaultImpl
specifier|private
specifier|static
name|Similarity
name|defaultImpl
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
DECL|field|NO_DOC_ID_PROVIDED
specifier|public
specifier|static
specifier|final
name|int
name|NO_DOC_ID_PROVIDED
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Set the default Similarity implementation used by indexing and search    * code.    *    * @see Searcher#setSimilarity(Similarity)    * @see org.apache.lucene.index.IndexWriter#setSimilarity(Similarity)    */
DECL|method|setDefault
specifier|public
specifier|static
name|void
name|setDefault
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|Similarity
operator|.
name|defaultImpl
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Return the default Similarity implementation used by indexing and search    * code.    *    *<p>This is initially an instance of {@link DefaultSimilarity}.    *    * @see Searcher#setSimilarity(Similarity)    * @see org.apache.lucene.index.IndexWriter#setSimilarity(Similarity)    */
DECL|method|getDefault
specifier|public
specifier|static
name|Similarity
name|getDefault
parameter_list|()
block|{
return|return
name|Similarity
operator|.
name|defaultImpl
return|;
block|}
comment|/** Cache of decoded bytes. */
DECL|field|NORM_TABLE
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|NORM_TABLE
init|=
operator|new
name|float
index|[
literal|256
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
name|NORM_TABLE
index|[
name|i
index|]
operator|=
name|SmallFloat
operator|.
name|byte315ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Decodes a normalization factor stored in an index.    * @see #encodeNorm(float)    */
DECL|method|decodeNorm
specifier|public
specifier|static
name|float
name|decodeNorm
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
name|b
operator|&
literal|0xFF
index|]
return|;
comment|//& 0xFF maps negative bytes to positive above 127
block|}
comment|/** Returns a table for decoding normalization bytes.    * @see #encodeNorm(float)    */
DECL|method|getNormDecoder
specifier|public
specifier|static
name|float
index|[]
name|getNormDecoder
parameter_list|()
block|{
return|return
name|NORM_TABLE
return|;
block|}
comment|/**    * Compute the normalization value for a field, given the accumulated    * state of term processing for this field (see {@link FieldInvertState}).    *     *<p>Implementations should calculate a float value based on the field    * state and then return that value.    *    *<p>For backward compatibility this method by default calls    * {@link #lengthNorm(String, int)} passing    * {@link FieldInvertState#getLength()} as the second argument, and    * then multiplies this value by {@link FieldInvertState#getBoost()}.</p>    *     *<p><b>WARNING</b>: This API is new and experimental and may    * suddenly change.</p>    *     * @param field field name    * @param state current processing state for this field    * @return the calculated float norm    */
DECL|method|computeNorm
specifier|public
name|float
name|computeNorm
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
operator|*
name|lengthNorm
argument_list|(
name|field
argument_list|,
name|state
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/** Computes the normalization value for a field given the total number of    * terms contained in a field.  These values, together with field boosts, are    * stored in an index and multipled into scores for hits on each field by the    * search code.    *    *<p>Matches in longer fields are less precise, so implementations of this    * method usually return smaller values when<code>numTokens</code> is large,    * and larger values when<code>numTokens</code> is small.    *     *<p>Note that the return values are computed under     * {@link org.apache.lucene.index.IndexWriter#addDocument(org.apache.lucene.document.Document)}     * and then stored using    * {@link #encodeNorm(float)}.      * Thus they have limited precision, and documents    * must be re-indexed if this method is altered.    *    * @param fieldName the name of the field    * @param numTokens the total number of tokens contained in fields named    *<i>fieldName</i> of<i>doc</i>.    * @return a normalization factor for hits on this field of this document    *    * @see org.apache.lucene.document.Field#setBoost(float)    */
DECL|method|lengthNorm
specifier|public
specifier|abstract
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTokens
parameter_list|)
function_decl|;
comment|/** Computes the normalization value for a query given the sum of the squared    * weights of each of the query terms.  This value is then multipled into the    * weight of each query term.    *    *<p>This does not affect ranking, but rather just attempts to make scores    * from different queries comparable.    *    * @param sumOfSquaredWeights the sum of the squares of query term weights    * @return a normalization factor for query weights    */
DECL|method|queryNorm
specifier|public
specifier|abstract
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
function_decl|;
comment|/** Encodes a normalization factor for storage in an index.    *    *<p>The encoding uses a three-bit mantissa, a five-bit exponent, and    * the zero-exponent point at 15, thus    * representing values from around 7x10^9 to 2x10^-9 with about one    * significant decimal digit of accuracy.  Zero is also represented.    * Negative numbers are rounded up to zero.  Values too large to represent    * are rounded down to the largest representable value.  Positive values too    * small to represent are rounded up to the smallest positive representable    * value.    *    * @see org.apache.lucene.document.Field#setBoost(float)    * @see org.apache.lucene.util.SmallFloat    */
DECL|method|encodeNorm
specifier|public
specifier|static
name|byte
name|encodeNorm
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** Computes a score factor based on a term or phrase's frequency in a    * document.  This value is multiplied by the {@link #idf(Term, Searcher)}    * factor for each term in the query and these products are then summed to    * form the initial score for a document.    *    *<p>Terms and phrases repeated in a document indicate the topic of the    * document, so implementations of this method usually return larger values    * when<code>freq</code> is large, and smaller values when<code>freq</code>    * is small.    *    *<p>The default implementation calls {@link #tf(float)}.    *    * @param freq the frequency of a term within a document    * @return a score factor based on a term's within-document frequency    */
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|int
name|freq
parameter_list|)
block|{
return|return
name|tf
argument_list|(
operator|(
name|float
operator|)
name|freq
argument_list|)
return|;
block|}
comment|/** Computes the amount of a sloppy phrase match, based on an edit distance.    * This value is summed for each sloppy phrase match in a document to form    * the frequency that is passed to {@link #tf(float)}.    *    *<p>A phrase match with a small edit distance to a document passage more    * closely matches the document, so implementations of this method usually    * return larger values when the edit distance is small and smaller values    * when it is large.    *    * @see PhraseQuery#setSlop(int)    * @param distance the edit distance of this sloppy phrase match    * @return the frequency increment for this match    */
DECL|method|sloppyFreq
specifier|public
specifier|abstract
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
function_decl|;
comment|/** Computes a score factor based on a term or phrase's frequency in a    * document.  This value is multiplied by the {@link #idf(Term, Searcher)}    * factor for each term in the query and these products are then summed to    * form the initial score for a document.    *    *<p>Terms and phrases repeated in a document indicate the topic of the    * document, so implementations of this method usually return larger values    * when<code>freq</code> is large, and smaller values when<code>freq</code>    * is small.    *    * @param freq the frequency of a term within a document    * @return a score factor based on a term's within-document frequency    */
DECL|method|tf
specifier|public
specifier|abstract
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
function_decl|;
comment|/** Computes a score factor for a simple term.    *    *<p>The default implementation is:<pre>    *   return idf(searcher.docFreq(term), searcher.maxDoc());    *</pre>    *    * Note that {@link Searcher#maxDoc()} is used instead of    * {@link org.apache.lucene.index.IndexReader#numDocs()} because it is proportional to    * {@link Searcher#docFreq(Term)} , i.e., when one is inaccurate,    * so is the other, and in the same direction.    *    * @param term the term in question    * @param searcher the document collection being searched    * @return a score factor for the term    */
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|Term
name|term
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|idf
argument_list|(
name|searcher
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
argument_list|,
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
comment|/** Computes a score factor for a phrase.    *    *<p>The default implementation sums the {@link #idf(Term,Searcher)} factor    * for each term in the phrase.    *    * @param terms the terms in the phrase    * @param searcher the document collection being searched    * @return a score factor for the phrase    */
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|Collection
name|terms
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|idf
init|=
literal|0.0f
decl_stmt|;
name|Iterator
name|i
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|idf
operator|+=
name|idf
argument_list|(
operator|(
name|Term
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
return|return
name|idf
return|;
block|}
comment|/** Computes a score factor based on a term's document frequency (the number    * of documents which contain the term).  This value is multiplied by the    * {@link #tf(int)} factor for each term in the query and these products are    * then summed to form the initial score for a document.    *    *<p>Terms that occur in fewer documents are better indicators of topic, so    * implementations of this method usually return larger values for rare terms,    * and smaller values for common terms.    *    * @param docFreq the number of documents which contain the term    * @param numDocs the total number of documents in the collection    * @return a score factor based on the term's document frequency    */
DECL|method|idf
specifier|public
specifier|abstract
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
function_decl|;
comment|/** Computes a score factor based on the fraction of all query terms that a    * document contains.  This value is multiplied into scores.    *    *<p>The presence of a large portion of the query terms indicates a better    * match with the query, so implementations of this method usually return    * larger values when the ratio between these parameters is large and smaller    * values when the ratio between them is small.    *    * @param overlap the number of query terms matched in the document    * @param maxOverlap the total number of terms in the query    * @return a score factor based on term overlap with the query    */
DECL|method|coord
specifier|public
specifier|abstract
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
function_decl|;
comment|/**    * Calculate a scoring factor based on the data in the payload.  Overriding implementations    * are responsible for interpreting what is in the payload.  Lucene makes no assumptions about    * what is in the byte array.    *<p>    * The default implementation returns 1.    *    * @param fieldName The fieldName of the term this payload belongs to    * @param payload The payload byte array to be scored    * @param offset The offset into the payload array    * @param length The length in the array    * @return An implementation dependent float to be used as a scoring factor    *    * @deprecated See {@link #scorePayload(int, String, int, int, byte[], int, int)}    */
comment|//TODO: When removing this, set the default value below to return 1.
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|//Do nothing
return|return
literal|1
return|;
block|}
comment|/**    * Calculate a scoring factor based on the data in the payload.  Overriding implementations    * are responsible for interpreting what is in the payload.  Lucene makes no assumptions about    * what is in the byte array.    *<p>    * The default implementation returns 1.    *    * @param docId The docId currently being scored.  If this value is {@link #NO_DOC_ID_PROVIDED}, then it should be assumed that the PayloadQuery implementation does not provide document information    * @param fieldName The fieldName of the term this payload belongs to    * @param start The start position of the payload    * @param end The end position of the payload    * @param payload The payload byte array to be scored    * @param offset The offset into the payload array    * @param length The length in the array    * @return An implementation dependent float to be used as a scoring factor    *    */
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|//TODO: When removing the deprecated scorePayload above, set this to return 1
return|return
name|scorePayload
argument_list|(
name|fieldName
argument_list|,
name|payload
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
end_class

end_unit

