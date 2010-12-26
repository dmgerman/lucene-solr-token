begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestNoMergeScheduler
specifier|public
class|class
name|TestNoMergeScheduler
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testNoMergeScheduler
specifier|public
name|void
name|testNoMergeScheduler
parameter_list|()
throws|throws
name|Exception
block|{
name|MergeScheduler
name|ms
init|=
name|NoMergeScheduler
operator|.
name|INSTANCE
decl_stmt|;
name|ms
operator|.
name|close
argument_list|()
expr_stmt|;
name|ms
operator|.
name|merge
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFinalSingleton
specifier|public
name|void
name|testFinalSingleton
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|Modifier
operator|.
name|isFinal
argument_list|(
name|NoMergeScheduler
operator|.
name|class
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
index|[]
name|ctors
init|=
name|NoMergeScheduler
operator|.
name|class
operator|.
name|getDeclaredConstructors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expected 1 private ctor only: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|ctors
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ctors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"that 1 should be private: "
operator|+
name|ctors
index|[
literal|0
index|]
argument_list|,
name|Modifier
operator|.
name|isPrivate
argument_list|(
name|ctors
index|[
literal|0
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMethodsOverridden
specifier|public
name|void
name|testMethodsOverridden
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Ensures that all methods of MergeScheduler are overridden. That's
comment|// important to ensure that NoMergeScheduler overrides everything, so that
comment|// no unexpected behavior/error occurs
for|for
control|(
name|Method
name|m
range|:
name|NoMergeScheduler
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
comment|// getDeclaredMethods() returns just those methods that are declared on
comment|// NoMergeScheduler. getMethods() returns those that are visible in that
comment|// context, including ones from Object. So just filter out Object. If in
comment|// the future MergeScheduler will extend a different class than Object,
comment|// this will need to change.
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|Object
operator|.
name|class
condition|)
block|{
name|assertTrue
argument_list|(
name|m
operator|+
literal|" is not overridden !"
argument_list|,
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|NoMergeScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

