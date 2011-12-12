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
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|FieldType
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
name|TextField
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Create an index with terms from 000-999.  * Generates random regexps according to simple patterns,  * and validates the correct number of hits are returned.  */
end_comment

begin_class
DECL|class|TestRegexpRandom
specifier|public
class|class
name|TestRegexpRandom
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|NumberFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"000"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setValue
argument_list|(
name|df
operator|.
name|format
argument_list|(
name|i
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|N
specifier|private
name|char
name|N
parameter_list|()
block|{
return|return
call|(
name|char
call|)
argument_list|(
literal|0x30
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fillPattern
specifier|private
name|String
name|fillPattern
parameter_list|(
name|String
name|wildcardPattern
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|wildcardPattern
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|wildcardPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'N'
case|:
name|sb
operator|.
name|append
argument_list|(
name|N
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|sb
operator|.
name|append
argument_list|(
name|wildcardPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assertPatternHits
specifier|private
name|void
name|assertPatternHits
parameter_list|(
name|String
name|pattern
parameter_list|,
name|int
name|numHits
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|wq
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|fillPattern
argument_list|(
name|pattern
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|wq
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect hits for pattern: "
operator|+
name|pattern
argument_list|,
name|numHits
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testRegexps
specifier|public
name|void
name|testRegexps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|assertPatternHits
argument_list|(
literal|"NNN"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".NN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N.N"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN."
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|assertPatternHits
argument_list|(
literal|".{1,2}N"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N.{1,2}"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".{1,3}"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN[3-7]"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N[2-6][3-7]"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"[1-5][2-6][3-7]"
argument_list|,
literal|125
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"[0-4][3-7][4-8]"
argument_list|,
literal|125
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"[2-6][0-4]N"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"[2-6]NN"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN.*"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N.*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*NN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*N"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N.*N"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// combo of ? and * operators
name|assertPatternHits
argument_list|(
literal|".N.*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N..*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*N."
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*.."
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|".*.N"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

