begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package

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
name|Field
operator|.
name|Index
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
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
name|gdata
operator|.
name|search
operator|.
name|index
operator|.
name|GdataIndexerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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

begin_class
DECL|class|TestContentStrategy
specifier|public
class|class
name|TestContentStrategy
extends|extends
name|TestCase
block|{
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"foo"
decl_stmt|;
DECL|field|BOOST
specifier|private
specifier|static
specifier|final
name|float
name|BOOST
init|=
literal|2.0f
decl_stmt|;
DECL|field|strategy
name|ContentStrategy
name|strategy
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSchemaField
name|field
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStore
argument_list|(
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|field
operator|.
name|setIndex
argument_list|(
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBoost
argument_list|(
name|BOOST
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|TestStrategy
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testContentStrategyIndexStoreField
specifier|public
name|void
name|testContentStrategyIndexStoreField
parameter_list|()
throws|throws
name|NotIndexableException
block|{
name|IndexSchemaField
name|field
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|TestStrategy
argument_list|(
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|this
operator|.
name|strategy
operator|.
name|createLuceneField
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|FIELD
argument_list|,
name|f
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestStrategy
operator|.
name|CONTENT
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|f
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isStored
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isTokenized
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.search.analysis.ContentStrategy.ContentStrategy(Index, Store, IndexSchemaField)'      */
DECL|method|testContentStrategyIndexSchemaField
specifier|public
name|void
name|testContentStrategyIndexSchemaField
parameter_list|()
throws|throws
name|NotIndexableException
block|{
name|IndexSchemaField
name|field
init|=
operator|new
name|IndexSchemaField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|TestStrategy
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|this
operator|.
name|strategy
operator|.
name|createLuceneField
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|FIELD
argument_list|,
name|f
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestStrategy
operator|.
name|CONTENT
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|f
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isStored
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isTokenized
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.search.analysis.ContentStrategy.createLuceneField()'      */
DECL|method|testCreateLuceneField
specifier|public
name|void
name|testCreateLuceneField
parameter_list|()
throws|throws
name|NotIndexableException
block|{
try|try
block|{
name|this
operator|.
name|strategy
operator|.
name|createLuceneField
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"processIndexable is not called"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GdataIndexerException
name|e
parameter_list|)
block|{
comment|//
block|}
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|this
operator|.
name|strategy
operator|.
name|createLuceneField
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|FIELD
argument_list|,
name|f
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestStrategy
operator|.
name|CONTENT
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BOOST
argument_list|,
name|f
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isStored
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isTokenized
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestStrategy
specifier|private
specifier|static
class|class
name|TestStrategy
extends|extends
name|ContentStrategy
block|{
DECL|field|CONTENT
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT
init|=
literal|"someString"
decl_stmt|;
DECL|method|TestStrategy
specifier|protected
name|TestStrategy
parameter_list|(
name|Index
name|index
parameter_list|,
name|Store
name|store
parameter_list|,
name|IndexSchemaField
name|fieldConfig
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|store
argument_list|,
name|fieldConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|TestStrategy
specifier|protected
name|TestStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|fieldConfiguration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processIndexable
specifier|public
name|void
name|processIndexable
parameter_list|(
name|Indexable
argument_list|<
name|?
extends|extends
name|Node
argument_list|,
name|?
extends|extends
name|ServerBaseEntry
argument_list|>
name|indexable
parameter_list|)
throws|throws
name|NotIndexableException
block|{
name|this
operator|.
name|content
operator|=
name|CONTENT
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

