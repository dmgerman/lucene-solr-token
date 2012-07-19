begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AtomicReader
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
name|AtomicReaderContext
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
name|DirectoryReader
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
name|MultiFields
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
name|Fields
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
name|ReaderUtil
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
name|index
operator|.
name|FieldsEnum
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
name|DocsEnum
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
name|DocIdSetIterator
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
name|FSDirectory
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
name|PriorityQueue
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
name|Bits
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  *   *<code>HighFreqTerms</code> class extracts the top n most frequent terms  * (by document frequency ) from an existing Lucene index and reports their document frequencey.  * If the -t flag is  and reports both their document frequency and their total tf (total number of occurences)   * in order of highest total tf  */
end_comment

begin_class
DECL|class|HighFreqTerms
specifier|public
class|class
name|HighFreqTerms
block|{
comment|// The top numTerms will be displayed
DECL|field|DEFAULTnumTerms
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULTnumTerms
init|=
literal|100
decl_stmt|;
DECL|field|numTerms
specifier|public
specifier|static
name|int
name|numTerms
init|=
name|DEFAULTnumTerms
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|FSDirectory
name|dir
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|boolean
name|IncludeTermFreqs
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
operator|||
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-t"
argument_list|)
condition|)
block|{
name|IncludeTermFreqs
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|numTerms
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|field
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|IncludeTermFreqs
condition|)
block|{
comment|//default HighFreqTerms behavior
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s:%s %,d \n"
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|termtext
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|TermStats
index|[]
name|termsWithTF
init|=
name|sortByTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termsWithTF
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s:%s \t totalTF = %,d \t doc freq = %,d \n"
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|field
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|termtext
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n"
operator|+
literal|"java org.apache.lucene.misc.HighFreqTerms<index dir> [-t] [number_terms] [field]\n\t -t: include totalTermFreq\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @param reader    * @param numTerms    * @param field    * @return TermStats[] ordered by terms with highest docFreq first.    * @throws Exception    */
DECL|method|getHighFreqTerms
specifier|public
specifier|static
name|TermStats
index|[]
name|getHighFreqTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|Exception
block|{
name|TermStatsQueue
name|tiq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field "
operator|+
name|field
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|tiq
operator|=
operator|new
name|TermStatsQueue
argument_list|(
name|numTerms
argument_list|)
expr_stmt|;
name|tiq
operator|.
name|fill
argument_list|(
name|field
argument_list|,
name|termsEnum
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no fields found for this index"
argument_list|)
throw|;
block|}
name|tiq
operator|=
operator|new
name|TermStatsQueue
argument_list|(
name|numTerms
argument_list|)
expr_stmt|;
name|FieldsEnum
name|fieldsEnum
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|field
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|tiq
operator|.
name|fill
argument_list|(
name|field
argument_list|,
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
name|TermStats
index|[]
name|result
init|=
operator|new
name|TermStats
index|[
name|tiq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// we want highest first so we read the queue and populate the array
comment|// starting at the end and work backwards
name|int
name|count
init|=
name|tiq
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|tiq
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|result
index|[
name|count
index|]
operator|=
name|tiq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Takes array of TermStats. For each term looks up the tf for each doc    * containing the term and stores the total in the output array of TermStats.    * Output array is sorted by highest total tf.    *     * @param reader    * @param terms    *          TermStats[]    * @return TermStats[]    * @throws Exception    */
DECL|method|sortByTotalTermFreq
specifier|public
specifier|static
name|TermStats
index|[]
name|sortByTotalTermFreq
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TermStats
index|[]
name|terms
parameter_list|)
throws|throws
name|Exception
block|{
name|TermStats
index|[]
name|ts
init|=
operator|new
name|TermStats
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
comment|// array for sorting
name|long
name|totalTF
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|totalTF
operator|=
name|getTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|termtext
argument_list|)
expr_stmt|;
name|ts
index|[
name|i
index|]
operator|=
operator|new
name|TermStats
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|termtext
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|,
name|totalTF
argument_list|)
expr_stmt|;
block|}
name|Comparator
argument_list|<
name|TermStats
argument_list|>
name|c
init|=
operator|new
name|TotalTermFreqComparatorSortDescending
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|ts
argument_list|,
name|c
argument_list|)
expr_stmt|;
return|return
name|ts
return|;
block|}
DECL|method|getTotalTermFreq
specifier|public
specifier|static
name|long
name|getTotalTermFreq
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|BytesRef
name|termText
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|totalTF
init|=
literal|0L
decl_stmt|;
for|for
control|(
specifier|final
name|AtomicReaderContext
name|ctx
range|:
name|reader
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|r
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|r
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
comment|// TODO: we could do this up front, during the scan
comment|// (next()), instead of after-the-fact here w/ seek,
comment|// if the codec supports it and there are no del
comment|// docs...
specifier|final
name|long
name|totTF
init|=
name|r
operator|.
name|totalTermFreq
argument_list|(
name|field
argument_list|,
name|termText
argument_list|)
decl_stmt|;
if|if
condition|(
name|totTF
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalTF
operator|+=
name|totTF
expr_stmt|;
continue|continue;
block|}
comment|// otherwise we fall-through
block|}
name|DocsEnum
name|de
init|=
name|r
operator|.
name|termDocsEnum
argument_list|(
name|liveDocs
argument_list|,
name|field
argument_list|,
name|termText
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|de
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
name|totalTF
operator|+=
name|de
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|totalTF
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Comparator  *   * Reverse of normal Comparator. i.e. returns 1 if a.totalTermFreq is less than  * b.totalTermFreq So we can sort in descending order of totalTermFreq  */
end_comment

begin_class
DECL|class|TotalTermFreqComparatorSortDescending
specifier|final
class|class
name|TotalTermFreqComparatorSortDescending
implements|implements
name|Comparator
argument_list|<
name|TermStats
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|TermStats
name|a
parameter_list|,
name|TermStats
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|totalTermFreq
operator|<
name|b
operator|.
name|totalTermFreq
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|totalTermFreq
operator|>
name|b
operator|.
name|totalTermFreq
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**  * Priority queue for TermStats objects ordered by docFreq  **/
end_comment

begin_class
DECL|class|TermStatsQueue
specifier|final
class|class
name|TermStatsQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermStats
argument_list|>
block|{
DECL|method|TermStatsQueue
name|TermStatsQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TermStats
name|termInfoA
parameter_list|,
name|TermStats
name|termInfoB
parameter_list|)
block|{
return|return
name|termInfoA
operator|.
name|docFreq
operator|<
name|termInfoB
operator|.
name|docFreq
return|;
block|}
DECL|method|fill
specifier|protected
name|void
name|fill
parameter_list|(
name|String
name|field
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
name|insertWithOverflow
argument_list|(
operator|new
name|TermStats
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

