begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
operator|.
name|utils
package|;
end_package

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
name|TermEnum
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

begin_comment
comment|/**  * Suggest Quality queries based on an index contents.  * Utility class, used for making quality test benchmarks.  */
end_comment

begin_class
DECL|class|QualityQueriesFinder
specifier|public
class|class
name|QualityQueriesFinder
block|{
DECL|field|newline
specifier|private
specifier|static
specifier|final
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|/**    * Constructor over a directory containing the index.    * @param dir directory containing the index we search for the quality test.     */
DECL|method|QualityQueriesFinder
specifier|private
name|QualityQueriesFinder
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/**    * @param args {index-dir}    * @throws IOException  if cannot access the index.    */
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
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java QualityQueriesFinder<index-dir>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|QualityQueriesFinder
name|qqf
init|=
operator|new
name|QualityQueriesFinder
argument_list|(
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
argument_list|)
decl_stmt|;
name|String
name|q
index|[]
init|=
name|qqf
operator|.
name|bestQueries
argument_list|(
literal|"body"
argument_list|,
literal|20
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
name|q
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
name|println
argument_list|(
name|newline
operator|+
name|formatQueryAsTrecTopic
argument_list|(
name|i
argument_list|,
name|q
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bestQueries
specifier|private
name|String
index|[]
name|bestQueries
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numQueries
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|words
index|[]
init|=
name|bestTerms
argument_list|(
literal|"body"
argument_list|,
literal|4
operator|*
name|numQueries
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|words
operator|.
name|length
decl_stmt|;
name|int
name|m
init|=
name|n
operator|/
literal|4
decl_stmt|;
name|String
name|res
index|[]
init|=
operator|new
name|String
index|[
name|m
index|]
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|words
index|[
name|i
index|]
operator|+
literal|" "
operator|+
name|words
index|[
name|m
operator|+
name|i
index|]
operator|+
literal|"  "
operator|+
name|words
index|[
name|n
operator|-
literal|1
operator|-
name|m
operator|-
name|i
index|]
operator|+
literal|" "
operator|+
name|words
index|[
name|n
operator|-
literal|1
operator|-
name|i
index|]
expr_stmt|;
comment|//System.out.println("query["+i+"]:  "+res[i]);
block|}
return|return
name|res
return|;
block|}
DECL|method|formatQueryAsTrecTopic
specifier|private
specifier|static
name|String
name|formatQueryAsTrecTopic
parameter_list|(
name|int
name|qnum
parameter_list|,
name|String
name|title
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|narrative
parameter_list|)
block|{
return|return
literal|"<top>"
operator|+
name|newline
operator|+
literal|"<num> Number: "
operator|+
name|qnum
operator|+
name|newline
operator|+
name|newline
operator|+
literal|"<title> "
operator|+
operator|(
name|title
operator|==
literal|null
condition|?
literal|""
else|:
name|title
operator|)
operator|+
name|newline
operator|+
name|newline
operator|+
literal|"<desc> Description:"
operator|+
name|newline
operator|+
operator|(
name|description
operator|==
literal|null
condition|?
literal|""
else|:
name|description
operator|)
operator|+
name|newline
operator|+
name|newline
operator|+
literal|"<narr> Narrative:"
operator|+
name|newline
operator|+
operator|(
name|narrative
operator|==
literal|null
condition|?
literal|""
else|:
name|narrative
operator|)
operator|+
name|newline
operator|+
name|newline
operator|+
literal|"</top>"
return|;
block|}
DECL|method|bestTerms
specifier|private
name|String
index|[]
name|bestTerms
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|PriorityQueue
name|pq
init|=
operator|new
name|TermsDfQueue
argument_list|(
name|numTerms
argument_list|)
decl_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|threshold
init|=
name|ir
operator|.
name|maxDoc
argument_list|()
operator|/
literal|10
decl_stmt|;
comment|// ignore words too common.
name|TermEnum
name|terms
init|=
name|ir
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|int
name|df
init|=
name|terms
operator|.
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|df
operator|<
name|threshold
condition|)
block|{
name|String
name|ttxt
init|=
name|terms
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
decl_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|TermDf
argument_list|(
name|ttxt
argument_list|,
name|df
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
name|res
index|[]
init|=
operator|new
name|String
index|[
name|pq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pq
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|TermDf
name|tdf
init|=
operator|(
name|TermDf
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|res
index|[
name|i
operator|++
index|]
operator|=
name|tdf
operator|.
name|word
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|".   word:  "
operator|+
name|tdf
operator|.
name|df
operator|+
literal|"   "
operator|+
name|tdf
operator|.
name|word
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|class|TermDf
specifier|private
specifier|static
class|class
name|TermDf
block|{
DECL|field|word
name|String
name|word
decl_stmt|;
DECL|field|df
name|int
name|df
decl_stmt|;
DECL|method|TermDf
name|TermDf
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|this
operator|.
name|word
operator|=
name|word
expr_stmt|;
name|this
operator|.
name|df
operator|=
name|freq
expr_stmt|;
block|}
block|}
DECL|class|TermsDfQueue
specifier|private
specifier|static
class|class
name|TermsDfQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|TermsDfQueue
name|TermsDfQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|initialize
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
name|TermDf
name|tf1
init|=
operator|(
name|TermDf
operator|)
name|a
decl_stmt|;
name|TermDf
name|tf2
init|=
operator|(
name|TermDf
operator|)
name|b
decl_stmt|;
return|return
name|tf1
operator|.
name|df
operator|<
name|tf2
operator|.
name|df
return|;
block|}
block|}
block|}
end_class

end_unit

