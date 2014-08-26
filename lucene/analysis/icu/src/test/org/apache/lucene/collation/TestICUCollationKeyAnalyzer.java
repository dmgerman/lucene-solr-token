begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|Analyzer
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
name|CollationTestBase
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_class
DECL|class|TestICUCollationKeyAnalyzer
specifier|public
class|class
name|TestICUCollationKeyAnalyzer
extends|extends
name|CollationTestBase
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"fa"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|ICUCollationKeyAnalyzer
argument_list|(
name|collator
argument_list|)
decl_stmt|;
DECL|field|firstRangeBeginning
specifier|private
name|BytesRef
name|firstRangeBeginning
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|firstRangeBeginningOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|firstRangeEnd
specifier|private
name|BytesRef
name|firstRangeEnd
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|firstRangeEndOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|secondRangeBeginning
specifier|private
name|BytesRef
name|secondRangeBeginning
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|secondRangeBeginningOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|secondRangeEnd
specifier|private
name|BytesRef
name|secondRangeEnd
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|secondRangeEndOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|testFarsiRangeFilterCollating
specifier|public
name|void
name|testFarsiRangeFilterCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiRangeFilterCollating
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsiRangeQueryCollating
specifier|public
name|void
name|testFarsiRangeQueryCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiRangeQueryCollating
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsiTermRangeQuery
specifier|public
name|void
name|testFarsiTermRangeQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiTermRangeQuery
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
DECL|method|testThreadSafe
specifier|public
name|void
name|testThreadSafe
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
literal|20
operator|*
name|RANDOM_MULTIPLIER
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Locale
name|locale
init|=
name|Locale
operator|.
name|GERMAN
decl_stmt|;
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
name|assertThreadSafe
argument_list|(
operator|new
name|ICUCollationKeyAnalyzer
argument_list|(
name|collator
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

