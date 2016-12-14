begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|stream
operator|.
name|DoubleStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|LongStream
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
name|DoubleDocValuesField
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
operator|.
name|Store
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
name|document
operator|.
name|StringField
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
name|search
operator|.
name|DocValuesStats
operator|.
name|DoubleDocValuesStats
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
name|DocValuesStats
operator|.
name|LongDocValuesStats
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
name|util
operator|.
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_comment
comment|/** Unit tests for {@link DocValuesStatsCollector}. */
end_comment

begin_class
DECL|class|TestDocValuesStatsCollector
specifier|public
class|class
name|TestDocValuesStatsCollector
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNoDocsWithField
specifier|public
name|void
name|testNoDocsWithField
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|;
name|IndexWriter
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
init|)
block|{
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|)
init|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|LongDocValuesStats
name|stats
init|=
operator|new
name|LongDocValuesStats
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|DocValuesStatsCollector
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|stats
operator|.
name|missing
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRandomDocsWithLongValues
specifier|public
name|void
name|testRandomDocsWithLongValues
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|;
name|IndexWriter
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
init|)
block|{
name|String
name|field
init|=
literal|"numeric"
decl_stmt|;
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|long
index|[]
name|docValues
init|=
operator|new
name|long
index|[
name|numDocs
index|]
decl_stmt|;
name|int
name|nextVal
init|=
literal|1
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
name|numDocs
condition|;
name|i
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// not all documents have a value
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
name|field
argument_list|,
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
index|[
name|i
index|]
operator|=
name|nextVal
expr_stmt|;
operator|++
name|nextVal
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// 20% of cases delete some docs
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.2
condition|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|indexWriter
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
try|try
init|(
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|)
init|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|LongDocValuesStats
name|stats
init|=
operator|new
name|LongDocValuesStats
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|DocValuesStatsCollector
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|expCount
init|=
operator|(
name|int
operator|)
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|>
literal|0
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expCount
argument_list|,
name|stats
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getZeroValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|count
argument_list|()
operator|-
name|reader
operator|.
name|numDeletedDocs
argument_list|()
argument_list|,
name|stats
operator|.
name|missing
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|max
argument_list|()
operator|.
name|getAsLong
argument_list|()
argument_list|,
name|stats
operator|.
name|max
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|min
argument_list|()
operator|.
name|getAsLong
argument_list|()
argument_list|,
name|stats
operator|.
name|min
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|average
argument_list|()
operator|.
name|getAsDouble
argument_list|()
argument_list|,
name|stats
operator|.
name|mean
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testRandomDocsWithDoubleValues
specifier|public
name|void
name|testRandomDocsWithDoubleValues
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|;
name|IndexWriter
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
init|)
block|{
name|String
name|field
init|=
literal|"numeric"
decl_stmt|;
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|double
index|[]
name|docValues
init|=
operator|new
name|double
index|[
name|numDocs
index|]
decl_stmt|;
name|double
name|nextVal
init|=
literal|1.0
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
name|numDocs
condition|;
name|i
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// not all documents have a value
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleDocValuesField
argument_list|(
name|field
argument_list|,
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
index|[
name|i
index|]
operator|=
name|nextVal
expr_stmt|;
operator|++
name|nextVal
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// 20% of cases delete some docs
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.2
condition|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|indexWriter
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
try|try
init|(
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|)
init|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DoubleDocValuesStats
name|stats
init|=
operator|new
name|DoubleDocValuesStats
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|DocValuesStatsCollector
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|expCount
init|=
operator|(
name|int
operator|)
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|>
literal|0
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expCount
argument_list|,
name|stats
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getZeroValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|count
argument_list|()
operator|-
name|reader
operator|.
name|numDeletedDocs
argument_list|()
argument_list|,
name|stats
operator|.
name|missing
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|max
argument_list|()
operator|.
name|getAsDouble
argument_list|()
argument_list|,
name|stats
operator|.
name|max
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|min
argument_list|()
operator|.
name|getAsDouble
argument_list|()
argument_list|,
name|stats
operator|.
name|min
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getPositiveValues
argument_list|(
name|docValues
argument_list|)
operator|.
name|average
argument_list|()
operator|.
name|getAsDouble
argument_list|()
argument_list|,
name|stats
operator|.
name|mean
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getPositiveValues
specifier|private
specifier|static
name|LongStream
name|getPositiveValues
parameter_list|(
name|long
index|[]
name|docValues
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|>
literal|0
argument_list|)
return|;
block|}
DECL|method|getPositiveValues
specifier|private
specifier|static
name|DoubleStream
name|getPositiveValues
parameter_list|(
name|double
index|[]
name|docValues
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|>
literal|0
argument_list|)
return|;
block|}
DECL|method|getZeroValues
specifier|private
specifier|static
name|LongStream
name|getZeroValues
parameter_list|(
name|long
index|[]
name|docValues
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|==
literal|0
argument_list|)
return|;
block|}
DECL|method|getZeroValues
specifier|private
specifier|static
name|DoubleStream
name|getZeroValues
parameter_list|(
name|double
index|[]
name|docValues
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|docValues
argument_list|)
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|==
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit
