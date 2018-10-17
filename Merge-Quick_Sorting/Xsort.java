/* #|
@author saustin4

@version 2018-April-10

@sources  http://www.java2novice.com/java-sorting-algorithms/merge-sort/
		  https://stackoverflow.com/questions/13727030/mergesort-in-java
		  https://www.sanfoundry.com/java-program-implement-merge-sort/
		  collaborated with James Caldwell on concepts and structures
		  

@license: CC-BY 4.0 -- you are free to share and adapt this file
for any purpose, provided you include appropriate attribution.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the *original* file satisfies "appropriate attribution".
|# */

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Xsort {

    static int x;
    static int y;
    static int locPointer = 0;
    static int fileUsed = 0;

    int blockC0unt;
    int index2 = 0;

    String finalN = "";

    String currentDir;
    File tempDir;

    File[] l1;
    File[] r1;

    static ArrayList<File> closeArray;

    File firstFile;

    RandomAccessFile l2;
    RandomAccessFile r2;



    RandomAccessFile xtract;
    RandomAccessFile l0ad;

    /**
     * Instantiates a new Xsort.
     *
     * @param xtrct the xtrct
     * @param b     the b
     * @param rec   the rec
     * @param load  the load
     */
    public Xsort(String xtrct, int b, int rec, String load) {

        this.x = b;
        this.y = rec;
        this.finalN = load;
        ParameterSet.DEFAULT = new ParameterSet(x, y);
        this.firstFile = new File(xtrct);

        currentDir = new File(".").getAbsolutePath();
         tempDir = new File(currentDir + "//tmpFiles");


        boolean isDirectoryCreated = tempDir.mkdir();

        //If directory exists, delete it, otherwise create it
        if (isDirectoryCreated) {
            System.out.println("Directory successfully made");

        } else {
            deleteDirectory(tempDir);
            tempDir.mkdir();
        }

        strt(firstFile);
    }

    /**
     * Strt.
     *
     * @param in the in
     */
    public void strt(File in) {

        closeArray = new ArrayList<File>();
        xtract = newRandAccFile(in);
        blockC0unt = blockGrab(xtract);

        //empty set and record of 1 test
        if (blockC0unt == 0) {
        } else if (blockC0unt == 1) {
            //If there's only 1 block, sort it and create l0ad
            try {
                byte[] inter = BlockIO.readBlock(xtract, 0);
                QuickSort.quicksort(inter, 0, x / y, y, new Random(0));
                l0ad = new RandomAccessFile(finalN, "rwd");
                BlockIO.writeBlock(l0ad, 0, inter);
                // 1 block
                l0ad.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (blockC0unt == 2) {
            l1 = new File[1];
            r1 = new File[1];
            firstSep(xtract);
            combining(l1[0], r1[0], l1, true);
        } else {
            l1 = new File[(blockC0unt / 2)];
            r1 = new File[blockC0unt - (blockC0unt / 2)];


            firstSep(xtract);

            System.out.println("Left Split");
            separate(l1);

            System.out.println("Right Split");
            separate(r1);
            closeFiles(xtract);
            closeFiles(l0ad);
            combining(l1[0], r1[0], l1, true);

            for (File temp : closeArray) {
                if (temp != null) {
                    temp.delete();
                }
            }


            deleteDirectory(tempDir);

        }
    }


    /**
     * Comb.
     *
     * @param lFile   the l file
     * @param rFile   the r file
     * @param arrFile the arr file
     */
    public void comb(File lFile, File rFile, File[] arrFile) {
        combining(lFile, rFile, arrFile, false);
    }

    /**
     * Combining.
     *
     * @param lFile2    the l file 2
     * @param rFile2    the r file 2
     * @param arrFile2  the arr file 2
     * @param lastMerge the last merge
     */
    public void combining(File lFile2, File rFile2, File[] arrFile2, Boolean lastMerge) {

        System.out.println("Combining files");
        System.out.println("Current Index: " + locPointer);

        String fIle = "";

        if (lastMerge == true) {
            fIle = finalN;
        } else {
            fIle = "tmpfiles/combined" + fileUsed + ".txt";
            fileUsed += 1;
        }

        File tempLeftFile = lFile2;
        File tempRightFile = rFile2;
        File merged = new File(fIle);


        l2 = newRandAccFile(tempLeftFile);
        r2 = newRandAccFile(tempRightFile);
        l0ad = newRandAccFile(merged);

        if (blockGrab(l2) == 1) {
            fileSort(l2);
        }
        if (blockGrab(r2) == 1) {
            fileSort(r2);
        }

        try {
            r2.seek(0);
            l2.seek(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] larr = new byte[x];
        byte[] rarr = new byte[x];
        byte[] blarr = new byte[x];

        File[] arrLast = arrFile2;

        int lNum = 0;
        int rNum = 0;

        int lFirst = 0;
        int lLast = y - 1;
        int rFirst = 0;
        int rLast = y - 1;

        int blarrFirst = 0;
        int finBLockNum = 0;

        int lBlockCOunt = blockGrab(l2);
        int rBlockCOunt = blockGrab(r2);


        try {
            larr = BlockIO.readBlock(l2, lNum);
            rarr = BlockIO.readBlock(r2, rNum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean done = false;
        while (done != true) {


            if ((lNum != lBlockCOunt) && (rNum != rBlockCOunt)) {
                if (Arrays.compare(larr, lFirst, lLast, rarr, rFirst, rLast) <= -1) {
                    System.arraycopy(larr, lFirst, blarr, blarrFirst, y);
                    lFirst += y;
                    lLast += y;
                    blarrFirst += y;
                } else if (Arrays.compare(larr, lFirst, lLast, rarr, rFirst, rLast) == 0) {
                    System.arraycopy(larr, lFirst, blarr, blarrFirst, y);
                    lFirst += y;
                    lLast += y;
                    blarrFirst += y;
                } else if (Arrays.compare(larr, lFirst, lLast, rarr, rFirst, rLast) >= 1) {
                    System.arraycopy(rarr, rFirst, blarr, blarrFirst, y);
                    rFirst += y;
                    rLast += y;
                    blarrFirst += y;
                }
                if (lFirst == x && lNum != lBlockCOunt) {
                    lNum += 1;
                    larr = arrInsert(l2, lNum);
                    lFirst = 0;
                    lLast = y - 1;
                } else if (rFirst == x && rNum != rBlockCOunt) {
                    rNum += 1;
                    rarr = arrInsert(r2, rNum);
                    rFirst = 0;
                    rLast = y - 1;
                }
                if (blarrFirst == x) {
                    arrCreate(l0ad, finBLockNum, blarr);
                    blarrFirst = 0;
                    finBLockNum += 1;
                }
            } else if (lNum == lBlockCOunt) {
                while (rFirst != x) {
                    System.arraycopy(rarr, rFirst, blarr, blarrFirst, y);
                    rFirst += y;
                    rLast += y;
                    blarrFirst += y;
                }
                arrCreate(l0ad, finBLockNum, blarr);

                finBLockNum += 1;
                rNum += 1;

            } else if (rNum == rBlockCOunt) {

                while (lFirst != x) {

                    System.arraycopy(larr, lFirst, blarr, blarrFirst, y);
                    lFirst += y;
                    lLast += y;
                    blarrFirst += y;
                }
                arrCreate(l0ad, finBLockNum, blarr);
                finBLockNum += 1;
                lNum += 1;
            }

            if (lNum == lBlockCOunt && rNum == rBlockCOunt) {
                done = true;
            }
        }


        closeFiles(l2);
        closeFiles(r2);

        closeArray.add(arrLast[locPointer]);
        arrLast[locPointer].delete();

        if (arrLast.length > 1 && arrLast[locPointer + 1] != null) {
            closeArray.add(arrLast[locPointer + 1]);

            arrLast[locPointer + 1].delete();

        }

        arrLast[locPointer] = merged;

        if (lastMerge == true) {
            l1[0] = merged;
            closeArray.add(r1[0]);

            r1[0].delete();
        }


        System.out.println("End of combination");

        closeFiles(l0ad);


    }

    /**
     * Arr create.
     *
     * @param initialFile the initial file
     * @param numOfBLocks the num of b locks
     * @param begArr      the beg arr
     */
    public void arrCreate(RandomAccessFile initialFile, int numOfBLocks, byte[] begArr) {

        try {
            BlockIO.writeBlock(initialFile, numOfBLocks, begArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arr insert byte [ ].
     *
     * @param _file     the file
     * @param _blockNum the block num
     *
     * @return the byte [ ]
     */
    byte[] arrInsert(RandomAccessFile _file, int _blockNum) {

        byte[] result = new byte[x];

        if (_blockNum < blockGrab(_file)) {
            try {
                result = BlockIO.readBlock(_file, _blockNum);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * New file random access file.
     *
     * @param nuFile the nu file
     *
     * @return the random access file
     */
    public RandomAccessFile newRandAccFile(File nuFile) {

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(nuFile, "rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;

    }

    /**
     * First sep.
     *
     * @param file the file
     */
    public void firstSep(RandomAccessFile file) {

        RandomAccessFile leftTemp;
        RandomAccessFile rightTemp;

        System.out.println("First Split");

        blockC0unt = blockGrab(file);
        String nam = "tmpfiles/tmp" + fileUsed + ".txt";
        File left = new File(nam);
        fileUsed++;
        nam = "tmpfiles/tmp" + fileUsed + ".txt";
        File right = new File(nam);
        fileUsed++;

        leftTemp = newRandAccFile(left);
        rightTemp = newRandAccFile(right);

        int l3 = l1.length;
        int r3 = r1.length;

        if (blockC0unt == 2) {
            l3 = 1;
            r3 = 1;
        }
        System.out.println(blockC0unt + " total blocks, " + l3 + " on left, " + r3 + " on right ");

        int offset = blockC0unt - r1.length;
        byte[] temp;

        for (int i = 0; i < l1.length; i++) {
            try {
                temp = BlockIO.readBlock(xtract, i);
                BlockIO.writeBlock(leftTemp, i, temp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (int j = 0; j < r1.length; j++) {

            try {
                temp = BlockIO.readBlock(xtract, j + offset);
                BlockIO.writeBlock(rightTemp, j, temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        l1[0] = left;
        r1[0] = right;
        closeFiles(file);
        closeFiles(leftTemp);
        closeFiles(rightTemp);
    }


    /**
     * Separate.
     *
     * @param array the array
     */
    public void separate(File[] array) {

        boolean finished = false;

        RandomAccessFile blocks = newRandAccFile(array[locPointer]);
        RandomAccessFile blocksPlus = null;
        RandomAccessFile blocksMinus = null;
        blockC0unt = blockGrab(blocks);
        System.out.println("Current blocks " + blockC0unt);
        while (finished == false) {

            blocks = newRandAccFile(array[locPointer]);
            if (locPointer < array.length && array[locPointer + 1] != null) {
                blocksPlus = newRandAccFile(array[locPointer + 1]);
            }
            if (locPointer > 0 && array[locPointer - 1] != null) {
                blocksMinus = newRandAccFile(array[locPointer - 1]);
            }


            blockC0unt = blockGrab(blocks);
            if (blockC0unt != 1) {
                System.out.println("Splitting files");
                splitter(array);
            } else {
                System.out.println("Done Splitting");
                blocks = newRandAccFile(array[locPointer]);
                fileSort(blocks);
                if (locPointer == 0 && array.length > 1) {
                    System.out.println("Index 0");
                    if (array[1] == null) {
                        System.out.println("Index 1 = 1");
                    } else if (blockGrab(blocksPlus) == 1) {
                        blocks = newRandAccFile(array[locPointer]);

                        System.out.println("Next element has 1 block");
                        comb(array[0], array[1], array);
                        left(array, 1);
                        locPointer += 1;
                    } else {
                        locPointer += 1;
                    }
                } else if (array.length > 1) {
                    System.out.println("Array is bigger than size 1");
                    if (blockGrab(blocksMinus) == 1) {

                        comb(array[locPointer - 1], array[locPointer], array);
                        System.out.println("Moving left, starting at current index -1");
                        left(array, -1);
                        if (array[locPointer] == null) {
                            locPointer = 0;
                            blocks = newRandAccFile(array[0]);
                            while (blockGrab(blocks) < array.length) {
                                blocks = newRandAccFile(array[0]);
                                if (array[locPointer + 1] != null) {
                                    comb(array[locPointer], array[locPointer + 1], array);
                                }
                                System.out.println("Moving left, starting at current index +1 ---");
                                moveLeft(array);
                                locPointer += 1;
                                if (array[locPointer + 1] == null) {
                                    locPointer = 0;
                                }
                            }
                        }
                    } else if (array[locPointer + 1] == null) {
                        locPointer = 0;
                        blocks = newRandAccFile(array[0]);
                        while (blockGrab(blocks) < array.length) {
                            blocks = newRandAccFile(array[0]);
                            if (array[locPointer + 1] != null) {
                                comb(array[locPointer], array[locPointer + 1], array);
                            }
                            System.out.println("Moving left, starting at current index +1, next element null");
                            moveLeft(array);
                            locPointer += 1;
                            if (array[locPointer + 1] == null) {
                                locPointer = 0;
                            }
                        }
                    } else {
                        System.out.println("Increase Pointer");
                        locPointer += 1;
                    }
                }
            }

            System.out.println("Current Index: " + locPointer);

            blocks = newRandAccFile(array[0]);
            if (blockGrab(blocks) >= array.length) {
                finished = true;

            }
        }

        closeFiles(blocks);
        closeFiles(blocksMinus);
        closeFiles(blocksPlus);

    }

    /**
     * Splitter.
     *
     * @param filez the filez
     */
    public void splitter(File[] filez) {


        blockC0unt = blockGrab(newRandAccFile(filez[locPointer]));
        String name = "tmpfiles/tmp" + fileUsed + ".txt";

        int l1 = blockC0unt / 2;
        int r1 = blockC0unt - (blockC0unt / 2);
        if (l1 < 1) {
            l1 = 1;
        }
        if (r1 < 1) {
            r1 = 1;
        }

        File leftFile = new File(name);
        fileUsed++;
        name = "tmpfiles/tmp" + fileUsed + ".txt";
        File rightFile = new File(name);
        fileUsed++;
        byte[] temp;

        RandomAccessFile tempLeft = newRandAccFile(leftFile);
        RandomAccessFile tempRight = newRandAccFile(rightFile);

        for (int i = 0; i < l1; i++) {

            try {
                temp = BlockIO.readBlock(newRandAccFile(filez[locPointer]), i);
                BlockIO.writeBlock(tempLeft, i, temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int z = l1; z < (r1 + l1); z++) {

            try {
                temp = BlockIO.readBlock(newRandAccFile(filez[locPointer]), z);
                BlockIO.writeBlock(tempRight, z - l1, temp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        if (filez[locPointer] != null) {
            System.out.println(index2);
            closeArray.add(filez[locPointer]);

        }

        closeFiles(tempLeft);
        closeFiles(tempRight);

        filez[locPointer].delete();
        filez[locPointer] = leftFile;
        if (filez.length > 2) {
            moveRight(filez);
        }
        filez[locPointer + 1] = rightFile;


    }

    /**
     * Move right.
     *
     * @param filez the filez
     */
    public static void moveRight(File[] filez) {

        int index = locPointer + 1;

        for (int i = filez.length - 1; i > index; i--) {
            filez[i] = filez[i - 1];
        }
        closeArray.add(filez[index]);
        filez[index] = null;
    }

    /**
     * Move left.
     *
     * @param filez the filez
     */
    public static void moveLeft(File[] filez) {
        left(filez, 1);
    }

    /**
     * Left.
     *
     * @param filez the filez
     * @param point the point
     */
    public static void left(File[] filez, int point) {
        int index = locPointer + point;

        for (int a = index; a < filez.length - 1; a++) {
            filez[a] = filez[a + 1];
        }
        closeArray.add(filez[filez.length - 1]);
        filez[filez.length - 1] = null;
    }

    /**
     * Block grab int.
     *
     * @param text the text
     *
     * @return the int
     */
    public static int blockGrab(RandomAccessFile text) {
        int result = 0;
        try {
            long allBlocks = BlockIO.numBlocks(text);
            result = (int) allBlocks;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Set file used.
     *
     * @param num the num
     */
    public static void setFileUsed(int num) {

        fileUsed += num;
    }

    /**
     * Get file used int.
     *
     * @return the int
     */
    public static int getFileUsed() {

        return fileUsed;
    }

    /**
     * Get index int.
     *
     * @return the int
     */
    public static int getIndex() {

        return locPointer;
    }

    /**
     * Close files.
     *
     * @param closer the closer
     */
    public void closeFiles(RandomAccessFile closer) {
        try {
            closer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

            System.out.println(e);
        }
    }

    /**
     * File sort.
     *
     * @param file the file
     */
    public static void fileSort(RandomAccessFile file) {

        byte[] temp;
        try {
            temp = BlockIO.readBlock(file, 0);
            QuickSort.quicksort(temp, 0, x / y, y, new Random(0));
            BlockIO.writeBlock(file, 0, temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete dir. Taken from https://stackoverflow.com/questions/12835285/create-directory-if-exists-delete-directory-and
     * -its-content-and-create-new-one
     *
     * @param dir the dir
     */
    public void deleteDir(File dir) {
        File[] files = dir.listFiles();

        for (File myFile : files) {
            if (myFile.isDirectory()) {
                deleteDir(myFile);
            }
            myFile.delete();

        }
    }

    //http://javarevisited.blogspot.com/2015/03/how-to-delete-directory-in-java-with-files.html#ixzz5DZKSTefy
    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        // either file or an empty directory
        System.out.println("removing file or directory : " + dir.getName());
        return dir.delete();
    }


}




