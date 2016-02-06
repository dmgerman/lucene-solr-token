begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|BinaryDocValuesField
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
name|SortedDocValuesField
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
name|LeafReaderContext
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
name|DocValuesType
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
name|IndexWriterConfig
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
name|RandomIndexWriter
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|BytesRefFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
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
name|BytesRefBuilder
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import

begin_class
DECL|class|TestDocValuesFieldSources
specifier|public
class|class
name|TestDocValuesFieldSources
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwConfig
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nDocs
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|id
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|f
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BINARY
case|:
name|f
operator|=
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|SORTED
case|:
name|f
operator|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC
case|:
name|f
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|vals
init|=
operator|new
name|Object
index|[
name|nDocs
index|]
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|d
argument_list|,
name|iwConfig
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
name|nDocs
condition|;
operator|++
name|i
control|)
block|{
name|id
operator|.
name|setLongValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SORTED
case|:
case|case
name|BINARY
case|:
do|do
block|{
name|vals
index|[
name|i
index|]
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|(
operator|(
name|String
operator|)
name|vals
index|[
name|i
index|]
operator|)
operator|.
name|isEmpty
argument_list|()
condition|)
do|;
name|f
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
name|String
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC
case|:
specifier|final
name|int
name|bitsPerValue
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|)
decl_stmt|;
comment|// keep it an int
name|vals
index|[
name|i
index|]
operator|=
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|setLongValue
argument_list|(
operator|(
name|Long
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|i
operator|%
literal|10
operator|==
literal|9
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leave
range|:
name|rd
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|FunctionValues
name|ids
init|=
operator|new
name|LongFieldSource
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|leave
argument_list|)
decl_stmt|;
specifier|final
name|ValueSource
name|vs
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BINARY
case|:
case|case
name|SORTED
case|:
name|vs
operator|=
operator|new
name|BytesRefFieldSource
argument_list|(
literal|"dv"
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC
case|:
name|vs
operator|=
operator|new
name|LongFieldSource
argument_list|(
literal|"dv"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|final
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|leave
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|leave
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|exists
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|vs
operator|instanceof
name|BytesRefFieldSource
condition|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|String
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|vs
operator|instanceof
name|LongFieldSource
condition|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Long
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|Object
name|expected
init|=
name|vals
index|[
name|ids
operator|.
name|intVal
argument_list|(
name|i
argument_list|)
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SORTED
case|:
name|values
operator|.
name|ordVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// no exception
name|assertTrue
argument_list|(
name|values
operator|.
name|numOrd
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
comment|// fall-through
case|case
name|BINARY
case|:
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
name|String
operator|)
name|expected
argument_list|)
argument_list|,
name|bytes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC
case|:
name|assertEquals
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|values
operator|.
name|longVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|DocValuesType
name|type
range|:
name|DocValuesType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|!=
name|DocValuesType
operator|.
name|SORTED_SET
operator|&&
name|type
operator|!=
name|DocValuesType
operator|.
name|SORTED_NUMERIC
operator|&&
name|type
operator|!=
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
name|test
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

