package bsuir.backup.manager;

public class OutOfSyncException extends FMException {

    public OutOfSyncException()
    {
        super("Folder is out of synch.");
    }

    public OutOfSyncException(String message)
    {
        super(message);
    }

    public OutOfSyncException(Throwable cause)
    {
        super(cause);
    }

    public OutOfSyncException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
