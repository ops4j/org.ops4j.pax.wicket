/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ops4j.pax.wicket.samples.issues.internal;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nmw
 */
public class IssuePageMounter implements PageMounter {

    private static Logger LOGGER = LoggerFactory.getLogger(IssuePageMounter.class);

    @Override
    public void addMountPoint(String path, Class<? extends Page> pageClass) {
        LOGGER.info("Called addMountPoint >" + path + "< >" + pageClass + "<");
    }

    @Override
    public List<MountPointInfo> getMountPoints() {
        List<MountPointInfo> mountPoints = new ArrayList<MountPointInfo>();
        mountPoints.add(new MountPointInfo() {

            @Override
            public String getPath() {
                return "IssueMounted";
            }

            @Override
            public Class<? extends Page> getPage() {
                LOGGER.info("GOT asked about my wicket page");

                return ManuallyMountedWithInBoundaryPage.class;
            }
        });

        return mountPoints;
    }

}
