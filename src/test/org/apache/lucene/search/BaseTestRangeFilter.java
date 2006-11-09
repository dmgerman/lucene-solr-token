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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|SimpleAnalyzer
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
name|index
operator|.
name|IndexWriter
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
name|RAMDirectory
import|;
end_import

begin_class
DECL|class|BaseTestRangeFilter
specifier|public
class|class
name|BaseTestRangeFilter
extends|extends
name|TestCase
block|{
DECL|field|F
specifier|public
specifier|static
specifier|final
name|boolean
name|F
init|=
literal|false
decl_stmt|;
DECL|field|T
specifier|public
specifier|static
specifier|final
name|boolean
name|T
init|=
literal|true
decl_stmt|;
DECL|field|index
name|RAMDirectory
name|index
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|101
argument_list|)
decl_stmt|;
comment|// use a set seed to test is deterministic
DECL|field|maxR
name|int
name|maxR
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|minR
name|int
name|minR
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|minId
name|int
name|minId
init|=
literal|0
decl_stmt|;
DECL|field|maxId
name|int
name|maxId
init|=
literal|10000
decl_stmt|;
DECL|field|intLength
specifier|static
specifier|final
name|int
name|intLength
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
comment|/**      * a simple padding function that should work with any int      */
DECL|method|pad
specifier|public
specifier|static
name|String
name|pad
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|(
literal|40
argument_list|)
decl_stmt|;
name|String
name|p
init|=
literal|"0"
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
name|p
operator|=
literal|"-"
expr_stmt|;
name|n
operator|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
name|n
operator|+
literal|1
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|i
operator|<=
name|intLength
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|BaseTestRangeFilter
specifier|public
name|BaseTestRangeFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|BaseTestRangeFilter
specifier|public
name|BaseTestRangeFilter
parameter_list|()
block|{
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|build
specifier|private
name|void
name|build
parameter_list|()
block|{
try|try
block|{
comment|/* build an index */
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|index
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
name|T
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
name|minId
init|;
name|d
operator|<=
name|maxId
condition|;
name|d
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|pad
argument_list|(
name|d
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|r
init|=
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxR
operator|<
name|r
condition|)
block|{
name|maxR
operator|=
name|r
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|<
name|minR
condition|)
block|{
name|minR
operator|=
name|r
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"rand"
argument_list|,
name|pad
argument_list|(
name|r
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can't build index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|testPad
specifier|public
name|void
name|testPad
parameter_list|()
block|{
name|int
index|[]
name|tests
init|=
operator|new
name|int
index|[]
block|{
operator|-
literal|9999999
block|,
operator|-
literal|99560
block|,
operator|-
literal|100
block|,
operator|-
literal|3
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|9
block|,
literal|10
block|,
literal|1000
block|,
literal|999999999
block|}
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
name|tests
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
name|a
init|=
name|tests
index|[
name|i
index|]
decl_stmt|;
name|int
name|b
init|=
name|tests
index|[
name|i
operator|+
literal|1
index|]
decl_stmt|;
name|String
name|aa
init|=
name|pad
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|String
name|bb
init|=
name|pad
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|String
name|label
init|=
name|a
operator|+
literal|":"
operator|+
name|aa
operator|+
literal|" vs "
operator|+
name|b
operator|+
literal|":"
operator|+
name|bb
decl_stmt|;
name|assertEquals
argument_list|(
literal|"length of "
operator|+
name|label
argument_list|,
name|aa
operator|.
name|length
argument_list|()
argument_list|,
name|bb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"compare less than "
operator|+
name|label
argument_list|,
name|aa
operator|.
name|compareTo
argument_list|(
name|bb
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

