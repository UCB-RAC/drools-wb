package org.drools.workbench.jcr2vfsmigration.migrater.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.drools.workbench.jcr2vfsmigration.vfs.IOServiceFactory.Migration.*;

/**
 * Generates a Path for every object that needs to be migrated.
 * Guarantees uniqueness. Supports look ups.
 */
@ApplicationScoped
public class MigrationPathManager {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private Map<String, Path> uuidToPathMap = new HashMap<String, Path>();
    private Map<Path, String> pathToUuidMap = new HashMap<Path, String>();
    private FileSystem fs;

    // Generate methods

    public Path generateRootPath() {

        final org.uberfire.java.nio.file.Path _path = getFileSystem().getPath( "/" );

        return Paths.convert( _path );

//        final Path path = PathFactory.newPath( Paths.convert( _path.getFileSystem() ), _path.getFileName().toString(), _path.toUri().toString() );
//        return path;
    }

    public Path generatePathForModule( Module jcrModule ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        final Path path = PathFactory.newPath( Paths.convert( modulePath.getFileSystem() ), modulePath.getFileName().toString(), modulePath.toUri().toString() );

        register( jcrModule.getUuid(), path );
        return path;
    }

    public Path generatePathForAsset( Module jcrModule,
                                      Asset jcrAsset,
                                      boolean hasDSL ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        //final org.uberfire.java.nio.file.Path directory = getPomDirectoryPath(pathToPom);
        org.uberfire.java.nio.file.Path assetPath = null;
        if ( AssetFormats.BUSINESS_RULE.equals( jcrAsset.getFormat() ) && !hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAsset.getName() + ".rdrl" );
        } else if ( AssetFormats.BUSINESS_RULE.equals( jcrAsset.getFormat() ) && hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAsset.getName() + ".rdslr" );
        } else {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAsset.getName() + "." + jcrAsset.getFormat() );
        }

        //final org.uberfire.java.nio.file.Path _path = fs.getPath( "/" + escapePathEntry( jcrModule.getName() ) + "/" + escapePathEntry( jcrAsset.getName() ) + "." + jcrAsset.getFormat() );

        final Path path = PathFactory.newPath( Paths.convert( assetPath.getFileSystem() ), assetPath.getFileName().toString(), assetPath.toUri().toString() );

        register( jcrAsset.getUuid(), path );
        return path;
    }

    public Path generatePathForAsset( Module jcrModule,
                                      Asset jcrAsset ) {
        return generatePathForAsset( jcrModule, jcrAsset, false );
    }

    public Path generatePathForAsset( Module jcrModule,
                                      AssetItem jcrAssetItem,
                                      boolean hasDSL ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        org.uberfire.java.nio.file.Path assetPath = null;
        if ( AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) && !hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAssetItem.getName() + ".rdrl" );
        } else if ( AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) && hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAssetItem.getName() + ".rdslr" );
        } else if ( AssetFormats.TEST_SCENARIO.equals( jcrAssetItem.getFormat() ) ) {
            assetPath = modulePath.resolve( "src/test/resources/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat() );
        } else {
            assetPath = modulePath.resolve( "src/main/resources/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat() );
        }

        final Path path = PathFactory.newPath( Paths.convert( assetPath.getFileSystem() ), assetPath.getFileName().toString(), assetPath.toUri().toString() );

        register( jcrAssetItem.getUUID(), path );
        return path;
    }

    public Path generatePathForAsset( Module jcrModule,
                                      AssetItem jcrAssetItem ) {
        return generatePathForAsset( jcrModule, jcrAssetItem, false );
    }

    private org.uberfire.java.nio.file.Path getPomDirectoryPath( final Path pathToPomXML ) {
        return Paths.convert( pathToPomXML ).getParent();
    }

    // Helper methods

    public String escapePathEntry( String pathEntry ) {
        // VFS doesn't support /'s in the path entries
        pathEntry = pathEntry.replaceAll( "/", " slash " );
        // TODO Once porcelli has a list of all illegal and escaped characters in PathEntry, deal with them here
        return pathEntry;
    }

    protected void register( String uuid,
                             Path path ) {
        if ( uuidToPathMap.containsKey( uuid ) ) {
            //already registered
            return;
/*            throw new IllegalArgumentException( "The uuid (" + uuid + ") cannot be registered for path ("
                                                        + path + ") because it has already been registered once. Last time it was for path ("
                                                        + uuidToPathMap.get( uuid ) + "), but even if it's equal, it should never be registered twice." );
*/
        }
        if ( pathToUuidMap.containsKey( path ) ) {
            //already registered
            return;
/*            throw new IllegalArgumentException( "The path (" + path + ") cannot be registered from uuid ("
                                                        + uuid + ") because it has already been registered once. Last time it was for uuid ("
                                                        + pathToUuidMap.get( path ) + "), but even if it's equal, it should never be registered twice." );
*/
        }
        uuidToPathMap.put( uuid, path );
        pathToUuidMap.put( path, uuid );
    }

    public Path getPath( String uuid ) {
        return uuidToPathMap.get( uuid );
    }

    public String getUuid( Path path ) {
        return pathToUuidMap.get( path );
    }

    public void setRepoName( final String repoName,
                             final String outputDir ) {
        URI uri = URI.create( "git://" + repoName );
        this.fs = ioService.newFileSystem( uri, new HashMap<String, Object>() {{
            put( "out-dir", outputDir );
        }}, MIGRATION_INSTANCE );
    }

    public FileSystem getFileSystem() {
        return fs;
    }

}
