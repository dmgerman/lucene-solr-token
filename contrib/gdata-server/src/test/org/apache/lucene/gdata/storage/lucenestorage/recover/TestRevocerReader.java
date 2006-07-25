begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage.recover
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|recover
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|StringReader
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
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
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
operator|.
name|StorageOperation
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
name|utils
operator|.
name|ProvidedServiceStub
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|DateTime
import|;
end_import

begin_class
DECL|class|TestRevocerReader
specifier|public
class|class
name|TestRevocerReader
extends|extends
name|TestCase
block|{
DECL|field|recReader
specifier|private
name|RecoverReader
name|recReader
decl_stmt|;
DECL|field|feedId
specifier|private
specifier|static
specifier|final
name|String
name|feedId
init|=
literal|"myFeed"
decl_stmt|;
DECL|field|entryId
specifier|private
specifier|static
specifier|final
name|String
name|entryId
init|=
literal|"myID"
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|static
specifier|final
name|Long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|title
specifier|private
name|String
name|title
init|=
literal|"myTitle"
decl_stmt|;
DECL|field|dateTime
specifier|private
specifier|static
specifier|final
name|DateTime
name|dateTime
init|=
name|DateTime
operator|.
name|now
argument_list|()
decl_stmt|;
DECL|field|delete
specifier|private
name|String
name|delete
init|=
literal|"D;"
operator|+
name|feedId
operator|+
literal|";"
operator|+
name|entryId
operator|+
literal|";"
operator|+
name|timestamp
operator|+
literal|";\n###########\n"
decl_stmt|;
DECL|field|insert
specifier|private
name|String
name|insert
init|=
literal|"I;"
operator|+
name|feedId
operator|+
literal|";"
operator|+
name|entryId
operator|+
literal|";"
operator|+
name|timestamp
operator|+
literal|";"
operator|+
name|ProvidedServiceStub
operator|.
name|SERVICE_NAME
operator|+
literal|";"
operator|+
name|RecoverWriter
operator|.
name|META_DATA_ENTRY_SEPARATOR
operator|+
literal|"<atom:entry xmlns:atom='http://www.w3.org/2005/Atom'><atom:id>"
operator|+
name|entryId
operator|+
literal|"</atom:id><atom:updated>"
operator|+
name|dateTime
operator|.
name|toString
argument_list|()
operator|+
literal|"</atom:updated><atom:title type='text'>"
operator|+
name|this
operator|.
name|title
operator|+
literal|"</atom:title></atom:entry>"
operator|+
name|RecoverWriter
operator|.
name|META_DATA_ENTRY_SEPARATOR
operator|+
name|RecoverWriter
operator|.
name|STORAGE_OPERATION_SEPARATOR
operator|+
name|RecoverWriter
operator|.
name|META_DATA_ENTRY_SEPARATOR
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|recReader
operator|=
operator|new
name|RecoverReader
argument_list|()
expr_stmt|;
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerService
argument_list|(
operator|new
name|ProvidedServiceStub
argument_list|()
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
comment|/*      * Test method for 'org.apache.lucene.gdata.storage.lucenestorage.recover.RecoverReader.getNonDeleteEntries(Reader)'      */
DECL|method|testRecoverDeletedEntries
specifier|public
name|void
name|testRecoverDeletedEntries
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|delete
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|recReader
operator|.
name|recoverEntries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|delete
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|recList
init|=
name|this
operator|.
name|recReader
operator|.
name|recoverEntries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|StorageEntryWrapper
name|delWrapper
init|=
name|recList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|StorageOperation
operator|.
name|DELETE
argument_list|,
name|delWrapper
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|feedId
argument_list|,
name|delWrapper
operator|.
name|getFeedId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entryId
argument_list|,
name|delWrapper
operator|.
name|getEntryId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecoverInsertedEntries
specifier|public
name|void
name|testRecoverInsertedEntries
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|insert
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|recList
init|=
name|this
operator|.
name|recReader
operator|.
name|recoverEntries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|StorageEntryWrapper
name|insWrapper
init|=
name|recList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|StorageOperation
operator|.
name|INSERT
argument_list|,
name|insWrapper
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|feedId
argument_list|,
name|insWrapper
operator|.
name|getFeedId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entryId
argument_list|,
name|insWrapper
operator|.
name|getEntryId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dateTime
argument_list|,
name|insWrapper
operator|.
name|getEntry
argument_list|()
operator|.
name|getUpdated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|title
argument_list|,
name|insWrapper
operator|.
name|getEntry
argument_list|()
operator|.
name|getTitle
argument_list|()
operator|.
name|getPlainText
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecoverReader
specifier|public
name|void
name|testRecoverReader
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|insert
operator|+
name|this
operator|.
name|delete
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|recList
init|=
name|this
operator|.
name|recReader
operator|.
name|recoverEntries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|recList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StorageOperation
operator|.
name|INSERT
argument_list|,
name|recList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StorageOperation
operator|.
name|DELETE
argument_list|,
name|recList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
literal|"some corrupted\n###########\n"
operator|+
name|this
operator|.
name|insert
argument_list|)
expr_stmt|;
name|recList
operator|=
name|this
operator|.
name|recReader
operator|.
name|recoverEntries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StorageOperation
operator|.
name|INSERT
argument_list|,
name|recList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

