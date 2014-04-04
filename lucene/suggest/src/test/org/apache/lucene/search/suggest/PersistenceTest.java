begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|suggest
operator|.
name|fst
operator|.
name|FSTCompletionLookup
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
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
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
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
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

begin_class
DECL|class|PersistenceTest
specifier|public
class|class
name|PersistenceTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|keys
specifier|public
specifier|final
name|String
index|[]
name|keys
init|=
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|,
literal|"four"
block|,
literal|"oneness"
block|,
literal|"onerous"
block|,
literal|"onesimus"
block|,
literal|"twofold"
block|,
literal|"twonk"
block|,
literal|"thrive"
block|,
literal|"through"
block|,
literal|"threat"
block|,
literal|"foundation"
block|,
literal|"fourier"
block|,
literal|"fourty"
block|}
decl_stmt|;
DECL|method|testTSTPersistence
specifier|public
name|void
name|testTSTPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|TSTLookup
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testJaspellPersistence
specifier|public
name|void
name|testJaspellPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|JaspellLookup
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testFSTPersistence
specifier|public
name|void
name|testFSTPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|FSTCompletionLookup
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|lookupClass
parameter_list|,
name|boolean
name|supportsExactWeights
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Add all input keys.
name|Lookup
name|lookup
init|=
name|lookupClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Input
index|[]
name|keys
init|=
operator|new
name|Input
index|[
name|this
operator|.
name|keys
operator|.
name|length
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|keys
index|[
name|i
index|]
operator|=
operator|new
name|Input
argument_list|(
name|this
operator|.
name|keys
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// Store the suggester.
name|File
name|storeDir
init|=
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|lookup
operator|.
name|store
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
literal|"lookup.dat"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-read it from disk.
name|lookup
operator|=
name|lookupClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
literal|"lookup.dat"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assert validity.
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|long
name|previous
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|Input
name|k
range|:
name|keys
control|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|list
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|TestUtil
operator|.
name|bytesToCharSequence
argument_list|(
name|k
operator|.
name|term
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LookupResult
name|lookupResult
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|k
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|lookupResult
operator|.
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|supportsExactWeights
condition|)
block|{
name|assertEquals
argument_list|(
name|k
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|k
operator|.
name|v
argument_list|,
name|lookupResult
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|lookupResult
operator|.
name|value
operator|+
literal|">="
operator|+
name|previous
argument_list|,
name|lookupResult
operator|.
name|value
operator|>=
name|previous
argument_list|)
expr_stmt|;
name|previous
operator|=
name|lookupResult
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

