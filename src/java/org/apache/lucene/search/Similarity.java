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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexWriter
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
name|document
operator|.
name|Field
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

begin_comment
comment|/** Expert: Scoring API.  *<p>Subclasses implement search scoring.  *  *<p>The score of query<code>q</code> for document<code>d</code> is defined  * in terms of these methods as follows:  *  *<table cellpadding="0" cellspacing="0" border="0">  *<tr>  *<td valign="middle" align="right" rowspan="2">score(q,d) =<br></td>  *<td valign="middle" align="center">  *<big><big><big><big><big>&Sigma;</big></big></big></big></big></td>  *<td valign="middle"><small>  *    {@link #tf(int) tf}(t in d) *  *    {@link #idf(Term,Searcher) idf}(t) *  *    {@link Field#getBoost getBoost}(t.field in d) *  *    {@link #lengthNorm(String,int) lengthNorm}(t.field in d)  *</small></td>  *<td valign="middle" rowspan="2">&nbsp;*  *    {@link #coord(int,int) coord}(q,d) *  *    {@link #queryNorm(float) queryNorm}(q)  *</td>  *</tr>  *<tr>  *<td valign="top" align="right">  *<small>t in q</small>  *</td>  *</tr>  *</table>  *  * @see #setDefault(Similarity)  * @see IndexWriter#setSimilarity(Similarity)  * @see Searcher#setSimilarity(Similarity)  */
end_comment

begin_class
DECL|class|Similarity
specifier|public
specifier|abstract
class|class
name|Similarity
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
comment|/** Set the default Similarity implementation used by indexing and search    * code.    *    * @see Searcher#setSimilarity(Similarity)    * @see IndexWriter#setSimilarity(Similarity)    */
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
comment|/** Return the default Similarity implementation used by indexing and search    * code.    *    *<p>This is initially an instance of {@link DefaultSimilarity}.    *    * @see Searcher#setSimilarity(Similarity)    * @see IndexWriter#setSimilarity(Similarity)    */
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
name|byteToFloat
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
comment|/** Computes the normalization value for a field given the total number of    * terms contained in a field.  These values, together with field boosts, are    * stored in an index and multipled into scores for hits on each field by the    * search code.    *    *<p>Matches in longer fields are less precise, so implemenations of this    * method usually return smaller values when<code>numTokens</code> is large,    * and larger values when<code>numTokens</code> is small.    *    *<p>That these values are computed under {@link    * IndexWriter#addDocument(org.apache.lucene.document.Document)} and stored then using    * {@link #encodeNorm(float)}.  Thus they have limited precision, and documents    * must be re-indexed if this method is altered.    *    * @param fieldName the name of the field    * @param numTokens the total number of tokens contained in fields named    *<i>fieldName</i> of<i>doc</i>.    * @return a normalization factor for hits on this field of this document    *    * @see Field#setBoost(float)    */
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
comment|/** Encodes a normalization factor for storage in an index.    *    *<p>The encoding uses a five-bit exponent and three-bit mantissa, thus    * representing values from around 7x10^9 to 2x10^-9 with about one    * significant decimal digit of accuracy.  Zero is also represented.    * Negative numbers are rounded up to zero.  Values too large to represent    * are rounded down to the largest representable value.  Positive values too    * small to represent are rounded up to the smallest positive representable    * value.    *    * @see Field#setBoost(float)    */
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
name|floatToByte
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|byteToFloat
specifier|private
specifier|static
name|float
name|byteToFloat
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
comment|// zero is a special case
return|return
literal|0.0f
return|;
name|int
name|mantissa
init|=
name|b
operator|&
literal|7
decl_stmt|;
name|int
name|exponent
init|=
operator|(
name|b
operator|>>
literal|3
operator|)
operator|&
literal|31
decl_stmt|;
name|int
name|bits
init|=
operator|(
operator|(
name|exponent
operator|+
operator|(
literal|63
operator|-
literal|15
operator|)
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
name|mantissa
operator|<<
literal|21
operator|)
decl_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
return|;
block|}
DECL|method|floatToByte
specifier|private
specifier|static
name|byte
name|floatToByte
parameter_list|(
name|float
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|<
literal|0.0f
condition|)
comment|// round negatives up to zero
name|f
operator|=
literal|0.0f
expr_stmt|;
if|if
condition|(
name|f
operator|==
literal|0.0f
condition|)
comment|// zero is a special case
return|return
literal|0
return|;
name|int
name|bits
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// parse float into parts
name|int
name|mantissa
init|=
operator|(
name|bits
operator|&
literal|0xffffff
operator|)
operator|>>
literal|21
decl_stmt|;
name|int
name|exponent
init|=
operator|(
operator|(
operator|(
name|bits
operator|>>
literal|24
operator|)
operator|&
literal|0x7f
operator|)
operator|-
literal|63
operator|)
operator|+
literal|15
decl_stmt|;
if|if
condition|(
name|exponent
operator|>
literal|31
condition|)
block|{
comment|// overflow: use max value
name|exponent
operator|=
literal|31
expr_stmt|;
name|mantissa
operator|=
literal|7
expr_stmt|;
block|}
if|if
condition|(
name|exponent
operator|<
literal|0
condition|)
block|{
comment|// underflow: use min value
name|exponent
operator|=
literal|0
expr_stmt|;
name|mantissa
operator|=
literal|1
expr_stmt|;
block|}
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|exponent
operator|<<
literal|3
operator|)
operator||
name|mantissa
argument_list|)
return|;
comment|// pack into a byte
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
comment|/** Computes a score factor based on a term or phrase's frequency in a    * document.  This value is multiplied by the {@link #idf(Term, Searcher)}    * factor for each term in the query and these products are then summed to    * form the initial score for a document.    *    *<p>Terms and phrases repeated in a document indicate the topic of the    * document, so implemenations of this method usually return larger values    * when<code>freq</code> is large, and smaller values when<code>freq</code>    * is small.    *    * @param freq the frequency of a term within a document    * @return a score factor based on a term's within-document frequency    */
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
comment|/** Computes a score factor for a simple term.    *    *<p>The default implementation is:<pre>    *   return idf(searcher.docFreq(term), searcher.maxDoc());    *</pre>    *    * Note that {@link Searcher#maxDoc()} is used instead of    * {@link IndexReader#numDocs()} because it is proportional to    * {@link Searcher#docFreq(Term)} , i.e., when one is inaccurate,    * so is the other, and in the same direction.    *    * @param term the term in question    * @param searcher the document collection being searched    * @return a score factor for the term    */
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
comment|/** Computes a score factor based on a term's document frequency (the number    * of documents which contain the term).  This value is multiplied by the    * {@link #tf(int)} factor for each term in the query and these products are    * then summed to form the initial score for a document.    *    *<p>Terms that occur in fewer documents are better indicators of topic, so    * implemenations of this method usually return larger values for rare terms,    * and smaller values for common terms.    *    * @param docFreq the number of documents which contain the term    * @param numDocs the total number of documents in the collection    * @return a score factor based on the term's document frequency    */
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
comment|/** Computes a score factor based on the fraction of all query terms that a    * document contains.  This value is multiplied into scores.    *    *<p>The presence of a large portion of the query terms indicates a better    * match with the query, so implemenations of this method usually return    * larger values when the ratio between these parameters is large and smaller    * values when the ratio between them is small.    *    * @param overlap the number of query terms matched in the document    * @param maxOverlap the total number of terms in the query    * @return a score factor based on term overlap with the query    */
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
block|}
end_class

end_unit

