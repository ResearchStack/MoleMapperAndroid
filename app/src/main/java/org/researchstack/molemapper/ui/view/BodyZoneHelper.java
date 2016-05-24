package org.researchstack.molemapper.ui.view;
import android.content.Context;
import android.graphics.PointF;

import org.researchstack.molemapper.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BodyZoneHelper
{

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Front
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final int FRONT_HEAD                   = 1100;
    public static final int FRONT_NECK_CENTER_CHEST      = 1200;
    public static final int FRONT_LEFT_PECTORAL          = 1251;
    public static final int FRONT_LEFT_ABDOMEN           = 1301;
    public static final int FRONT_LEFT_PELVIS            = 1351;
    public static final int FRONT_LEFT_UPPER_THIGH       = 1401;
    public static final int FRONT_LEFT_LOWER_THIGH_KNEE  = 1451;
    public static final int FRONT_LEFT_UPPER_CALF        = 1501;
    public static final int FRONT_LEFT_LOWER_CALF        = 1551;
    public static final int FRONT_LEFT_ANKLE_FOOT        = 1601;
    public static final int FRONT_LEFT_SHOULDER          = 1651;
    public static final int FRONT_LEFT_UPPER_ARM         = 1701;
    public static final int FRONT_LEFT_UPPER_FOREARM     = 1751;
    public static final int FRONT_LEFT_LOWER_FOREARM     = 1801;
    public static final int FRONT_LEFT_HAND              = 1851;
    public static final int FRONT_RIGHT_PECTORAL         = 1250;
    public static final int FRONT_RIGHT_ABDOMEN          = 1300;
    public static final int FRONT_RIGHT_PELVIS           = 1350;
    public static final int FRONT_RIGHT_UPPER_THIGH      = 1400;
    public static final int FRONT_RIGHT_LOWER_THIGH_KNEE = 1450;
    public static final int FRONT_RIGHT_UPPER_CALF       = 1500;
    public static final int FRONT_RIGHT_LOWER_CALF       = 1550;
    public static final int FRONT_RIGHT_ANKLE_FOOT       = 1600;
    public static final int FRONT_RIGHT_SHOULDER         = 1650;
    public static final int FRONT_RIGHT_UPPER_ARM        = 1700;
    public static final int FRONT_RIGHT_UPPER_FOREARM    = 1750;
    public static final int FRONT_RIGHT_LOWER_FOREARM    = 1800;
    public static final int FRONT_RIGHT_HAND             = 1850;
    public static final int ID_FRONT_START               = FRONT_HEAD;
    public static final int ID_FRONT_END                 = FRONT_LEFT_HAND;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Back
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final int BACK_HEAD                   = 2100;
    public static final int BACK_NECK                   = 2200;
    public static final int BACK_LEFT_UPPER_BACK        = 2250;
    public static final int BACK_LEFT_LOWER_BACK        = 2300;
    public static final int BACK_LEFT_GLUTE             = 2350;
    public static final int BACK_LEFT_UPPER_THIGH       = 2400;
    public static final int BACK_LEFT_LOWER_THIGH_KNEE  = 2450;
    public static final int BACK_LEFT_UPPER_CALF        = 2500;
    public static final int BACK_LEFT_LOWER_CALF        = 2550;
    public static final int BACK_LEFT_ANKLE_FOOT        = 2600;
    public static final int BACK_LEFT_SHOULDER          = 2650;
    public static final int BACK_LEFT_UPPER_ARM         = 2700;
    public static final int BACK_LEFT_ELBOW             = 2750;
    public static final int BACK_LEFT_LOWER_FOREARM     = 2800;
    public static final int BACK_LEFT_HAND              = 2850;
    public static final int BACK_RIGHT_UPPER_BACK       = 2251;
    public static final int BACK_RIGHT_LOWER_BACK       = 2301;
    public static final int BACK_RIGHT_GLUTE            = 2351;
    public static final int BACK_RIGHT_UPPER_THIGH      = 2401;
    public static final int BACK_RIGHT_LOWER_THIGH_KNEE = 2451;
    public static final int BACK_RIGHT_UPPER_CALF       = 2501;
    public static final int BACK_RIGHT_LOWER_CALF       = 2551;
    public static final int BACK_RIGHT_ANKLE_FOOT       = 2601;
    public static final int BACK_RIGHT_SHOULDER         = 2651;
    public static final int BACK_RIGHT_UPPER_ARM        = 2701;
    public static final int BACK_RIGHT_ELBOW            = 2751;
    public static final int BACK_RIGHT_LOWER_FOREARM    = 2801;
    public static final int BACK_RIGHT_HAND             = 2851;
    public static final int ID_BACK_START               = BACK_HEAD;
    public static final int ID_BACK_END                 = BACK_RIGHT_HAND;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Head
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final int FACE_LEFT_SIDE  = 3150;
    public static final int FACE_RIGHT_SIDE = 3151;
    public static final int HEAD_TOP        = 3170;
    public static final int FACE_FRONT      = 3171;
    public static final int HEAD_BACK       = 3172;
    public static final int ID_HEAD_START   = FACE_LEFT_SIDE;
    public static final int ID_HEAD_END     = HEAD_BACK;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Coordinates w/ range [0,1]
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    public static HashMap<Integer, ArrayList<PointF>> POINTS_BACK = new HashMap<>();

    public static ArrayList<PointF> POINTS_BACK_HEAD                   = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_NECK                   = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_SHOULDER          = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_UPPER_ARM         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_ELBOW             = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_LOWER_FOREARM     = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_HAND              = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_UPPER_BACK        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_LOWER_BACK        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_GLUTE             = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_UPPER_THIGH       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_LOWER_THIGH_KNEE  = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_UPPER_CALF        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_LOWER_CALF        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_LEFT_ANKLE_FOOT        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_SHOULDER         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_UPPER_ARM        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_ELBOW            = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_LOWER_FOREARM    = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_HAND             = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_UPPER_BACK       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_LOWER_BACK       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_GLUTE            = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_UPPER_THIGH      = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_LOWER_THIGH_KNEE = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_UPPER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_LOWER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_BACK_RIGHT_ANKLE_FOOT       = new ArrayList<>();

    public static HashMap<Integer, ArrayList<PointF>> POINTS_FRONT = new HashMap<>();

    public static ArrayList<PointF> POINTS_FRONT_HEAD                  = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_NECK_CENTER_CHEST     = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_SHOULDER         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_UPPER_ARM        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_UPPER_FOREARM    = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_LOWER_FOREARM    = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_HAND             = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_PECTORAL         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_ABDOMEN          = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_PELVIS           = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_UPPER_THIGH      = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_LOWER_THIGH_KNEE = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_UPPER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_LOWER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_LEFT_ANKLE_FOOT       = new ArrayList<>();

    public static ArrayList<PointF> POINTS_FRONT_RIGHT_SHOULDER         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_UPPER_ARM        = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_UPPER_FOREARM    = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_LOWER_FOREARM    = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_HAND             = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_PECTORAL         = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_ABDOMEN          = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_PELVIS           = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_UPPER_THIGH      = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_UPPER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_LOWER_CALF       = new ArrayList<>();
    public static ArrayList<PointF> POINTS_FRONT_RIGHT_ANKLE_FOOT       = new ArrayList<>();

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Text Offset (offset from center of region, for x & y axis, ranging [-1,1]
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static HashMap<Integer, PointF> TEXT_OFFSET_FRONT = new HashMap<>();

    public static HashMap<Integer, PointF> TEXT_OFFSET_BACK = new HashMap<>();

    static
    {
        initFront();
        initFrontTextOffset();

        initBack();
        initBackTextOffset();
    }

    private static void initFront()
    {
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Front Center
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        POINTS_FRONT_HEAD.add(new PointF(0.41296f, 0.01413f));
        POINTS_FRONT_HEAD.add(new PointF(0.59109f, 0.01413f));
        POINTS_FRONT_HEAD.add(new PointF(0.59109f, 0.12609f));
        POINTS_FRONT_HEAD.add(new PointF(0.41296f, 0.12609f));
        POINTS_FRONT.put(FRONT_HEAD, POINTS_FRONT_HEAD);

        POINTS_FRONT_NECK_CENTER_CHEST.add(new PointF(0.43725f, 0.12609f));
        POINTS_FRONT_NECK_CENTER_CHEST.add(new PointF(0.56680f, 0.12609f));
        POINTS_FRONT_NECK_CENTER_CHEST.add(new PointF(0.52024f, 0.27500f));
        POINTS_FRONT_NECK_CENTER_CHEST.add(new PointF(0.48381f, 0.27500f));
        POINTS_FRONT.put(FRONT_NECK_CENTER_CHEST, POINTS_FRONT_NECK_CENTER_CHEST);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Front Left
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.68826f, 0.18043f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.55870f, 0.15326f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.54858f, 0.18478f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.63158f, 0.20326f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.64980f, 0.26087f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.74494f, 0.24239f));
        POINTS_FRONT_LEFT_SHOULDER.add(new PointF(0.74089f, 0.21304f));
        POINTS_FRONT.put(FRONT_LEFT_SHOULDER, POINTS_FRONT_LEFT_SHOULDER);

        POINTS_FRONT_LEFT_UPPER_ARM.add(new PointF(0.80162f, 0.33152f));
        POINTS_FRONT_LEFT_UPPER_ARM.add(new PointF(0.70648f, 0.35000f));
        POINTS_FRONT_LEFT_UPPER_ARM.add(new PointF(0.65587f, 0.27500f));
        POINTS_FRONT_LEFT_UPPER_ARM.add(new PointF(0.64980f, 0.26087f));
        POINTS_FRONT_LEFT_UPPER_ARM.add(new PointF(0.74494f, 0.24239f));
        POINTS_FRONT.put(FRONT_LEFT_UPPER_ARM, POINTS_FRONT_LEFT_UPPER_ARM);

        POINTS_FRONT_LEFT_UPPER_FOREARM.add(new PointF(0.80162f, 0.33152f));
        POINTS_FRONT_LEFT_UPPER_FOREARM.add(new PointF(0.70648f, 0.35000f));
        POINTS_FRONT_LEFT_UPPER_FOREARM.add(new PointF(0.75506f, 0.41413f));
        POINTS_FRONT_LEFT_UPPER_FOREARM.add(new PointF(0.84615f, 0.39348f));
        POINTS_FRONT.put(FRONT_LEFT_UPPER_FOREARM, POINTS_FRONT_LEFT_UPPER_FOREARM);

        POINTS_FRONT_LEFT_LOWER_FOREARM.add(new PointF(0.84008f, 0.39457f));
        POINTS_FRONT_LEFT_LOWER_FOREARM.add(new PointF(0.75911f, 0.41304f));
        POINTS_FRONT_LEFT_LOWER_FOREARM.add(new PointF(0.80769f, 0.47609f));
        POINTS_FRONT_LEFT_LOWER_FOREARM.add(new PointF(0.87652f, 0.46304f));
        POINTS_FRONT.put(FRONT_LEFT_LOWER_FOREARM, POINTS_FRONT_LEFT_LOWER_FOREARM);

        POINTS_FRONT_LEFT_HAND.add(new PointF(0.87652f, 0.46304f));
        POINTS_FRONT_LEFT_HAND.add(new PointF(0.80769f, 0.47609f));
        POINTS_FRONT_LEFT_HAND.add(new PointF(0.80972f, 0.49783f));
        POINTS_FRONT_LEFT_HAND.add(new PointF(0.81781f, 0.56739f));
        POINTS_FRONT_LEFT_HAND.add(new PointF(0.94534f, 0.58043f));
        POINTS_FRONT_LEFT_HAND.add(new PointF(0.97368f, 0.50326f));
        POINTS_FRONT.put(FRONT_LEFT_HAND, POINTS_FRONT_LEFT_HAND);

        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.63158f, 0.20326f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.54858f, 0.18478f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.52024f, 0.27500f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.50202f, 0.27500f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.50202f, 0.32283f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.65385f, 0.32283f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.65385f, 0.29457f));
        POINTS_FRONT_LEFT_PECTORAL.add(new PointF(0.64980f, 0.26087f));
        POINTS_FRONT.put(FRONT_LEFT_PECTORAL, POINTS_FRONT_LEFT_PECTORAL);

        POINTS_FRONT_LEFT_ABDOMEN.add(new PointF(0.65385f, 0.32283f));
        POINTS_FRONT_LEFT_ABDOMEN.add(new PointF(0.50202f, 0.32283f));
        POINTS_FRONT_LEFT_ABDOMEN.add(new PointF(0.50202f, 0.41848f));
        POINTS_FRONT_LEFT_ABDOMEN.add(new PointF(0.65587f, 0.41848f));
        POINTS_FRONT.put(FRONT_LEFT_ABDOMEN, POINTS_FRONT_LEFT_ABDOMEN);

        POINTS_FRONT_LEFT_PELVIS.add(new PointF(0.65587f, 0.41848f));
        POINTS_FRONT_LEFT_PELVIS.add(new PointF(0.50000f, 0.41848f));
        POINTS_FRONT_LEFT_PELVIS.add(new PointF(0.50000f, 0.53913f));
        POINTS_FRONT_LEFT_PELVIS.add(new PointF(0.69231f, 0.50435f));
        POINTS_FRONT.put(FRONT_LEFT_PELVIS, POINTS_FRONT_LEFT_PELVIS);

        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.69231f, 0.50435f));
        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.50000f, 0.53913f));
        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.53036f, 0.62826f));
        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.68421f, 0.62826f));
        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.69231f, 0.59457f));
        POINTS_FRONT_LEFT_UPPER_THIGH.add(new PointF(0.69838f, 0.54457f));
        POINTS_FRONT.put(FRONT_LEFT_UPPER_THIGH, POINTS_FRONT_LEFT_UPPER_THIGH);

        POINTS_FRONT_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.68219f, 0.62826f));
        POINTS_FRONT_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.53036f, 0.62826f));
        POINTS_FRONT_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.55668f, 0.73587f));
        POINTS_FRONT_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.67611f, 0.73587f));
        POINTS_FRONT.put(FRONT_LEFT_LOWER_THIGH_KNEE, POINTS_FRONT_LEFT_LOWER_THIGH_KNEE);

        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.67611f, 0.73587f));
        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.55668f, 0.73587f));
        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.54858f, 0.78804f));
        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.55466f, 0.81196f));
        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.66599f, 0.81196f));
        POINTS_FRONT_LEFT_UPPER_CALF.add(new PointF(0.68016f, 0.78587f));
        POINTS_FRONT.put(FRONT_LEFT_UPPER_CALF, POINTS_FRONT_LEFT_UPPER_CALF);

        POINTS_FRONT_LEFT_LOWER_CALF.add(new PointF(0.66599f, 0.81196f));
        POINTS_FRONT_LEFT_LOWER_CALF.add(new PointF(0.55466f, 0.81196f));
        POINTS_FRONT_LEFT_LOWER_CALF.add(new PointF(0.58097f, 0.88043f));
        POINTS_FRONT_LEFT_LOWER_CALF.add(new PointF(0.64372f, 0.88043f));
        POINTS_FRONT.put(FRONT_LEFT_LOWER_CALF, POINTS_FRONT_LEFT_LOWER_CALF);

        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.64372f, 0.88043f));
        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.58097f, 0.88043f));
        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.56073f, 0.91196f));
        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.57085f, 0.98478f));
        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.68219f, 0.98370f));
        POINTS_FRONT_LEFT_ANKLE_FOOT.add(new PointF(0.68219f, 0.96196f));
        POINTS_FRONT.put(FRONT_LEFT_ANKLE_FOOT, POINTS_FRONT_LEFT_ANKLE_FOOT);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Front Right
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.31579f, 0.18043f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.44534f, 0.15326f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.45547f, 0.18478f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.37247f, 0.20326f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.35425f, 0.26087f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.25911f, 0.24239f));
        POINTS_FRONT_RIGHT_SHOULDER.add(new PointF(0.26316f, 0.21304f));
        POINTS_FRONT.put(FRONT_RIGHT_SHOULDER, POINTS_FRONT_RIGHT_SHOULDER);

        POINTS_FRONT_RIGHT_UPPER_ARM.add(new PointF(0.20243f, 0.33152f));
        POINTS_FRONT_RIGHT_UPPER_ARM.add(new PointF(0.29757f, 0.35000f));
        POINTS_FRONT_RIGHT_UPPER_ARM.add(new PointF(0.34818f, 0.27500f));
        POINTS_FRONT_RIGHT_UPPER_ARM.add(new PointF(0.35425f, 0.26087f));
        POINTS_FRONT_RIGHT_UPPER_ARM.add(new PointF(0.25911f, 0.24239f));
        POINTS_FRONT.put(FRONT_RIGHT_UPPER_ARM, POINTS_FRONT_RIGHT_UPPER_ARM);

        POINTS_FRONT_RIGHT_UPPER_FOREARM.add(new PointF(0.20243f, 0.33152f));
        POINTS_FRONT_RIGHT_UPPER_FOREARM.add(new PointF(0.29757f, 0.35000f));
        POINTS_FRONT_RIGHT_UPPER_FOREARM.add(new PointF(0.24899f, 0.41413f));
        POINTS_FRONT_RIGHT_UPPER_FOREARM.add(new PointF(0.15789f, 0.39348f));
        POINTS_FRONT.put(FRONT_RIGHT_UPPER_FOREARM, POINTS_FRONT_RIGHT_UPPER_FOREARM);

        POINTS_FRONT_RIGHT_LOWER_FOREARM.add(new PointF(0.16397f, 0.39457f));
        POINTS_FRONT_RIGHT_LOWER_FOREARM.add(new PointF(0.24494f, 0.41304f));
        POINTS_FRONT_RIGHT_LOWER_FOREARM.add(new PointF(0.19636f, 0.47609f));
        POINTS_FRONT_RIGHT_LOWER_FOREARM.add(new PointF(0.12753f, 0.46304f));
        POINTS_FRONT.put(FRONT_RIGHT_LOWER_FOREARM, POINTS_FRONT_RIGHT_LOWER_FOREARM);

        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.12753f, 0.46304f));
        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.19636f, 0.47609f));
        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.19433f, 0.49783f));
        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.18623f, 0.56739f));
        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.05870f, 0.58043f));
        POINTS_FRONT_RIGHT_HAND.add(new PointF(0.03036f, 0.50326f));
        POINTS_FRONT.put(FRONT_RIGHT_HAND, POINTS_FRONT_RIGHT_HAND);

        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.37247f, 0.20326f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.45547f, 0.18478f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.48381f, 0.27500f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.50202f, 0.27500f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.50202f, 0.32283f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.35020f, 0.32283f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.35020f, 0.29457f));
        POINTS_FRONT_RIGHT_PECTORAL.add(new PointF(0.35425f, 0.26087f));
        POINTS_FRONT.put(FRONT_RIGHT_PECTORAL, POINTS_FRONT_RIGHT_PECTORAL);

        POINTS_FRONT_RIGHT_ABDOMEN.add(new PointF(0.35020f, 0.32283f));
        POINTS_FRONT_RIGHT_ABDOMEN.add(new PointF(0.50202f, 0.32283f));
        POINTS_FRONT_RIGHT_ABDOMEN.add(new PointF(0.50202f, 0.41848f));
        POINTS_FRONT_RIGHT_ABDOMEN.add(new PointF(0.34818f, 0.41848f));
        POINTS_FRONT.put(FRONT_RIGHT_ABDOMEN, POINTS_FRONT_RIGHT_ABDOMEN);

        POINTS_FRONT_RIGHT_PELVIS.add(new PointF(0.34818f, 0.41848f));
        POINTS_FRONT_RIGHT_PELVIS.add(new PointF(0.50000f, 0.41848f));
        POINTS_FRONT_RIGHT_PELVIS.add(new PointF(0.50000f, 0.53913f));
        POINTS_FRONT_RIGHT_PELVIS.add(new PointF(0.31174f, 0.50435f));
        POINTS_FRONT.put(FRONT_RIGHT_PELVIS, POINTS_FRONT_RIGHT_PELVIS);

        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.31174f, 0.50435f));
        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.50202f, 0.53913f));
        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.47368f, 0.62826f));
        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.31984f, 0.62826f));
        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.31174f, 0.59457f));
        POINTS_FRONT_RIGHT_UPPER_THIGH.add(new PointF(0.30567f, 0.54457f));
        POINTS_FRONT.put(FRONT_RIGHT_UPPER_THIGH, POINTS_FRONT_RIGHT_UPPER_THIGH);

        POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.32186f, 0.62826f));
        POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.47368f, 0.62826f));
        POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.44737f, 0.73587f));
        POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.32794f, 0.73587f));
        POINTS_FRONT.put(FRONT_RIGHT_LOWER_THIGH_KNEE, POINTS_FRONT_RIGHT_LOWER_THIGH_KNEE);

        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.32794f, 0.73587f));
        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.44737f, 0.73587f));
        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.45547f, 0.78804f));
        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.44939f, 0.81196f));
        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.33806f, 0.81196f));
        POINTS_FRONT_RIGHT_UPPER_CALF.add(new PointF(0.32389f, 0.78587f));
        POINTS_FRONT.put(FRONT_RIGHT_UPPER_CALF, POINTS_FRONT_RIGHT_UPPER_CALF);

        POINTS_FRONT_RIGHT_LOWER_CALF.add(new PointF(0.33806f, 0.81196f));
        POINTS_FRONT_RIGHT_LOWER_CALF.add(new PointF(0.44939f, 0.81196f));
        POINTS_FRONT_RIGHT_LOWER_CALF.add(new PointF(0.42308f, 0.88043f));
        POINTS_FRONT_RIGHT_LOWER_CALF.add(new PointF(0.36032f, 0.88043f));
        POINTS_FRONT.put(FRONT_RIGHT_LOWER_CALF, POINTS_FRONT_RIGHT_LOWER_CALF);

        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.36032f, 0.88043f));
        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.42308f, 0.88043f));
        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.44332f, 0.91196f));
        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.43320f, 0.98478f));
        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.32186f, 0.98370f));
        POINTS_FRONT_RIGHT_ANKLE_FOOT.add(new PointF(0.32186f, 0.96196f));
        POINTS_FRONT.put(FRONT_RIGHT_ANKLE_FOOT, POINTS_FRONT_RIGHT_ANKLE_FOOT);

    }

    private static void initFrontTextOffset()
    {
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Front Left
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        TEXT_OFFSET_FRONT.put(FRONT_LEFT_SHOULDER, new PointF(0.21649f, 0.15151f));
        TEXT_OFFSET_FRONT.put(FRONT_LEFT_HAND, new PointF(- 0.13414f, - 0.13889f));
        TEXT_OFFSET_FRONT.put(FRONT_LEFT_ANKLE_FOOT, new PointF(0, .26041f));

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Front Right
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        TEXT_OFFSET_FRONT.put(FRONT_RIGHT_SHOULDER, new PointF(- 0.21649f, 0.15151f));
        TEXT_OFFSET_FRONT.put(FRONT_RIGHT_HAND, new PointF(0.13414f, - 0.13889f));
        TEXT_OFFSET_FRONT.put(FRONT_RIGHT_ANKLE_FOOT, new PointF(0, .26041f));
    }

    private static void initBackTextOffset()
    {
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Back Left
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        TEXT_OFFSET_BACK.put(BACK_LEFT_SHOULDER, new PointF(- 0.21649f, 0.15151f));
        TEXT_OFFSET_BACK.put(BACK_LEFT_HAND, new PointF(0.13414f, - 0.13889f));
        TEXT_OFFSET_BACK.put(BACK_LEFT_ANKLE_FOOT, new PointF(.07017f, - .02061f));

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Back Right
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        TEXT_OFFSET_BACK.put(BACK_RIGHT_SHOULDER, new PointF(0.21649f, 0.15151f));
        TEXT_OFFSET_BACK.put(BACK_RIGHT_HAND, new PointF(- 0.13414f, - 0.13889f));
        TEXT_OFFSET_BACK.put(BACK_RIGHT_ANKLE_FOOT, new PointF(- .07017f, - .02061f));
    }

    private static void initBack()
    {
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Back Center
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        POINTS_BACK_HEAD.add(new PointF(0.41903f, 0.01413f));
        POINTS_BACK_HEAD.add(new PointF(0.41903f, 0.13043f));
        POINTS_BACK_HEAD.add(new PointF(0.59109f, 0.13043f));
        POINTS_BACK_HEAD.add(new PointF(0.59109f, 0.01413f));
        POINTS_BACK.put(BACK_HEAD, POINTS_BACK_HEAD);

        POINTS_BACK_NECK.add(new PointF(0.43725f, 0.13043f));
        POINTS_BACK_NECK.add(new PointF(0.45547f, 0.18804f));
        POINTS_BACK_NECK.add(new PointF(0.54858f, 0.18804f));
        POINTS_BACK_NECK.add(new PointF(0.56275f, 0.13043f));
        POINTS_BACK.put(BACK_NECK, POINTS_BACK_NECK);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Back Left side
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.37854f, 0.20543f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.45547f, 0.18804f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.50202f, 0.18804f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.50202f, 0.32609f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.35830f, 0.32609f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.35628f, 0.30000f));
        POINTS_BACK_LEFT_UPPER_BACK.add(new PointF(0.36032f, 0.26522f));
        POINTS_BACK.put(BACK_LEFT_UPPER_BACK, POINTS_BACK_LEFT_UPPER_BACK);

        POINTS_BACK_LEFT_LOWER_BACK.add(new PointF(0.35830f, 0.32609f));
        POINTS_BACK_LEFT_LOWER_BACK.add(new PointF(0.50202f, 0.32609f));
        POINTS_BACK_LEFT_LOWER_BACK.add(new PointF(0.50202f, 0.42283f));
        POINTS_BACK_LEFT_LOWER_BACK.add(new PointF(0.35425f, 0.42283f));
        POINTS_BACK.put(BACK_LEFT_LOWER_BACK, POINTS_BACK_LEFT_LOWER_BACK);

        POINTS_BACK_LEFT_GLUTE.add(new PointF(0.35425f, 0.42283f));
        POINTS_BACK_LEFT_GLUTE.add(new PointF(0.50202f, 0.42283f));
        POINTS_BACK_LEFT_GLUTE.add(new PointF(0.50202f, 0.54457f));
        POINTS_BACK_LEFT_GLUTE.add(new PointF(0.31377f, 0.54457f));
        POINTS_BACK_LEFT_GLUTE.add(new PointF(0.31781f, 0.50761f));
        POINTS_BACK.put(BACK_LEFT_GLUTE, POINTS_BACK_LEFT_GLUTE);

        POINTS_BACK_LEFT_UPPER_THIGH.add(new PointF(0.31377f, 0.54457f));
        POINTS_BACK_LEFT_UPPER_THIGH.add(new PointF(0.50405f, 0.54457f));
        POINTS_BACK_LEFT_UPPER_THIGH.add(new PointF(0.47773f, 0.63152f));
        POINTS_BACK_LEFT_UPPER_THIGH.add(new PointF(0.32996f, 0.63152f));
        POINTS_BACK_LEFT_UPPER_THIGH.add(new PointF(0.31984f, 0.60109f));
        POINTS_BACK.put(BACK_LEFT_UPPER_THIGH, POINTS_BACK_LEFT_UPPER_THIGH);

        POINTS_BACK_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.32996f, 0.63152f));
        POINTS_BACK_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.47773f, 0.63152f));
        POINTS_BACK_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.45344f, 0.74022f));
        POINTS_BACK_LEFT_LOWER_THIGH_KNEE.add(new PointF(0.33806f, 0.74022f));
        POINTS_BACK.put(BACK_LEFT_LOWER_THIGH_KNEE, POINTS_BACK_LEFT_LOWER_THIGH_KNEE);

        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.33806f, 0.74022f));
        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.45344f, 0.74022f));
        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.45951f, 0.78587f));
        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.45547f, 0.81522f));
        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.34615f, 0.81522f));
        POINTS_BACK_LEFT_UPPER_CALF.add(new PointF(0.32996f, 0.76957f));
        POINTS_BACK.put(BACK_LEFT_UPPER_CALF, POINTS_BACK_LEFT_UPPER_CALF);

        POINTS_BACK_LEFT_LOWER_CALF.add(new PointF(0.34615f, 0.81522f));
        POINTS_BACK_LEFT_LOWER_CALF.add(new PointF(0.45547f, 0.81522f));
        POINTS_BACK_LEFT_LOWER_CALF.add(new PointF(0.43117f, 0.88370f));
        POINTS_BACK_LEFT_LOWER_CALF.add(new PointF(0.36842f, 0.88370f));
        POINTS_BACK.put(BACK_LEFT_LOWER_CALF, POINTS_BACK_LEFT_LOWER_CALF);

        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.36842f, 0.88370f));
        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.43117f, 0.88370f));
        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.45142f, 0.95326f));
        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.42713f, 0.98913f));
        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.35223f, 0.98913f));
        POINTS_BACK_LEFT_ANKLE_FOOT.add(new PointF(0.33603f, 0.96087f));
        POINTS_BACK.put(BACK_LEFT_ANKLE_FOOT, POINTS_BACK_LEFT_ANKLE_FOOT);

        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.44534f, 0.15761f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.32996f, 0.17935f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.28340f, 0.20652f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.26721f, 0.24565f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.36032f, 0.26522f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.37854f, 0.20543f));
        POINTS_BACK_LEFT_SHOULDER.add(new PointF(0.45547f, 0.18804f));
        POINTS_BACK.put(BACK_LEFT_SHOULDER, POINTS_BACK_LEFT_SHOULDER);

        POINTS_BACK_LEFT_UPPER_ARM.add(new PointF(0.26721f, 0.24565f));
        POINTS_BACK_LEFT_UPPER_ARM.add(new PointF(0.21053f, 0.33587f));
        POINTS_BACK_LEFT_UPPER_ARM.add(new PointF(0.30162f, 0.35543f));
        POINTS_BACK_LEFT_UPPER_ARM.add(new PointF(0.36032f, 0.26522f));
        POINTS_BACK.put(BACK_LEFT_UPPER_ARM, POINTS_BACK_LEFT_UPPER_ARM);

        POINTS_BACK_LEFT_ELBOW.add(new PointF(0.21053f, 0.33587f));
        POINTS_BACK_LEFT_ELBOW.add(new PointF(0.16802f, 0.39783f));
        POINTS_BACK_LEFT_ELBOW.add(new PointF(0.25709f, 0.41848f));
        POINTS_BACK_LEFT_ELBOW.add(new PointF(0.30162f, 0.35543f));
        POINTS_BACK.put(BACK_LEFT_ELBOW, POINTS_BACK_LEFT_ELBOW);

        POINTS_BACK_LEFT_LOWER_FOREARM.add(new PointF(0.17611f, 0.40000f));
        POINTS_BACK_LEFT_LOWER_FOREARM.add(new PointF(0.13765f, 0.46739f));
        POINTS_BACK_LEFT_LOWER_FOREARM.add(new PointF(0.20445f, 0.48043f));
        POINTS_BACK_LEFT_LOWER_FOREARM.add(new PointF(0.25304f, 0.41739f));
        POINTS_BACK.put(BACK_LEFT_LOWER_FOREARM, POINTS_BACK_LEFT_LOWER_FOREARM);

        POINTS_BACK_LEFT_HAND.add(new PointF(0.13765f, 0.46739f));
        POINTS_BACK_LEFT_HAND.add(new PointF(0.03846f, 0.50761f));
        POINTS_BACK_LEFT_HAND.add(new PointF(0.07287f, 0.57935f));
        POINTS_BACK_LEFT_HAND.add(new PointF(0.19433f, 0.57174f));
        POINTS_BACK_LEFT_HAND.add(new PointF(0.20648f, 0.50217f));
        POINTS_BACK_LEFT_HAND.add(new PointF(0.20445f, 0.48043f));
        POINTS_BACK.put(BACK_LEFT_HAND, POINTS_BACK_LEFT_HAND);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Back Right side
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.67611f, 0.17935f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.55668f, 0.15326f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.54858f, 0.18804f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.63158f, 0.20652f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.64980f, 0.26522f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.74291f, 0.24565f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.73482f, 0.22065f));
        POINTS_BACK_RIGHT_SHOULDER.add(new PointF(0.72065f, 0.20217f));
        POINTS_BACK.put(BACK_RIGHT_SHOULDER, POINTS_BACK_RIGHT_SHOULDER);

        POINTS_BACK_RIGHT_UPPER_ARM.add(new PointF(0.79960f, 0.33478f));
        POINTS_BACK_RIGHT_UPPER_ARM.add(new PointF(0.70648f, 0.35326f));
        POINTS_BACK_RIGHT_UPPER_ARM.add(new PointF(0.64777f, 0.26522f));
        POINTS_BACK_RIGHT_UPPER_ARM.add(new PointF(0.74291f, 0.24565f));
        POINTS_BACK.put(BACK_RIGHT_UPPER_ARM, POINTS_BACK_RIGHT_UPPER_ARM);

        POINTS_BACK_RIGHT_ELBOW.add(new PointF(0.79960f, 0.33478f));
        POINTS_BACK_RIGHT_ELBOW.add(new PointF(0.70648f, 0.35326f));
        POINTS_BACK_RIGHT_ELBOW.add(new PointF(0.74899f, 0.41957f));
        POINTS_BACK_RIGHT_ELBOW.add(new PointF(0.84211f, 0.39891f));
        POINTS_BACK.put(BACK_RIGHT_ELBOW, POINTS_BACK_RIGHT_ELBOW);

        POINTS_BACK_RIGHT_LOWER_FOREARM.add(new PointF(0.83603f, 0.40000f));
        POINTS_BACK_RIGHT_LOWER_FOREARM.add(new PointF(0.75709f, 0.41739f));
        POINTS_BACK_RIGHT_LOWER_FOREARM.add(new PointF(0.80567f, 0.47935f));
        POINTS_BACK_RIGHT_LOWER_FOREARM.add(new PointF(0.87045f, 0.46630f));
        POINTS_BACK.put(BACK_RIGHT_LOWER_FOREARM, POINTS_BACK_RIGHT_LOWER_FOREARM);

        POINTS_BACK_RIGHT_HAND.add(new PointF(0.87045f, 0.46630f));
        POINTS_BACK_RIGHT_HAND.add(new PointF(0.80567f, 0.47935f));
        POINTS_BACK_RIGHT_HAND.add(new PointF(0.80162f, 0.48913f));
        POINTS_BACK_RIGHT_HAND.add(new PointF(0.81174f, 0.57174f));
        POINTS_BACK_RIGHT_HAND.add(new PointF(0.93320f, 0.57826f));
        POINTS_BACK_RIGHT_HAND.add(new PointF(0.96964f, 0.50543f));
        POINTS_BACK.put(BACK_RIGHT_HAND, POINTS_BACK_RIGHT_HAND);

        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.63158f, 0.20652f));
        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.54858f, 0.18804f));
        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.50202f, 0.18804f));
        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.50202f, 0.32609f));
        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.65385f, 0.32609f));
        POINTS_BACK_RIGHT_UPPER_BACK.add(new PointF(0.65385f, 0.27935f));
        POINTS_BACK.put(BACK_RIGHT_UPPER_BACK, POINTS_BACK_RIGHT_UPPER_BACK);

        POINTS_BACK_RIGHT_LOWER_BACK.add(new PointF(0.65385f, 0.32609f));
        POINTS_BACK_RIGHT_LOWER_BACK.add(new PointF(0.50202f, 0.32609f));
        POINTS_BACK_RIGHT_LOWER_BACK.add(new PointF(0.50202f, 0.42283f));
        POINTS_BACK_RIGHT_LOWER_BACK.add(new PointF(0.65587f, 0.42283f));
        POINTS_BACK.put(BACK_RIGHT_LOWER_BACK, POINTS_BACK_RIGHT_LOWER_BACK);

        POINTS_BACK_RIGHT_GLUTE.add(new PointF(0.65587f, 0.42283f));
        POINTS_BACK_RIGHT_GLUTE.add(new PointF(0.50202f, 0.42283f));
        POINTS_BACK_RIGHT_GLUTE.add(new PointF(0.50202f, 0.54457f));
        POINTS_BACK_RIGHT_GLUTE.add(new PointF(0.69433f, 0.54457f));
        POINTS_BACK_RIGHT_GLUTE.add(new PointF(0.69433f, 0.50761f));
        POINTS_BACK.put(BACK_RIGHT_GLUTE, POINTS_BACK_RIGHT_GLUTE);

        POINTS_BACK_RIGHT_UPPER_THIGH.add(new PointF(0.69636f, 0.54457f));
        POINTS_BACK_RIGHT_UPPER_THIGH.add(new PointF(0.50607f, 0.54457f));
        POINTS_BACK_RIGHT_UPPER_THIGH.add(new PointF(0.53239f, 0.63152f));
        POINTS_BACK_RIGHT_UPPER_THIGH.add(new PointF(0.68016f, 0.63152f));
        POINTS_BACK_RIGHT_UPPER_THIGH.add(new PointF(0.69028f, 0.60109f));
        POINTS_BACK.put(BACK_RIGHT_UPPER_THIGH, POINTS_BACK_RIGHT_UPPER_THIGH);

        POINTS_BACK_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.68016f, 0.63152f));
        POINTS_BACK_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.53239f, 0.63152f));
        POINTS_BACK_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.55668f, 0.74022f));
        POINTS_BACK_RIGHT_LOWER_THIGH_KNEE.add(new PointF(0.67409f, 0.74022f));
        POINTS_BACK.put(BACK_RIGHT_LOWER_THIGH_KNEE, POINTS_BACK_RIGHT_LOWER_THIGH_KNEE);

        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.67611f, 0.74022f));
        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.55870f, 0.74022f));
        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.55263f, 0.78587f));
        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.55668f, 0.81522f));
        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.66599f, 0.81522f));
        POINTS_BACK_RIGHT_UPPER_CALF.add(new PointF(0.68016f, 0.77065f));
        POINTS_BACK.put(BACK_RIGHT_UPPER_CALF, POINTS_BACK_RIGHT_UPPER_CALF);

        POINTS_BACK_RIGHT_LOWER_CALF.add(new PointF(0.66599f, 0.81522f));
        POINTS_BACK_RIGHT_LOWER_CALF.add(new PointF(0.55668f, 0.81522f));
        POINTS_BACK_RIGHT_LOWER_CALF.add(new PointF(0.58097f, 0.88370f));
        POINTS_BACK_RIGHT_LOWER_CALF.add(new PointF(0.64575f, 0.88370f));
        POINTS_BACK.put(BACK_RIGHT_LOWER_CALF, POINTS_BACK_RIGHT_LOWER_CALF);

        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.64575f, 0.88370f));
        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.58097f, 0.88370f));
        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.56275f, 0.95326f));
        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.58300f, 0.98913f));
        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.65587f, 0.98913f));
        POINTS_BACK_RIGHT_ANKLE_FOOT.add(new PointF(0.67409f, 0.96087f));
        POINTS_BACK.put(BACK_RIGHT_ANKLE_FOOT, POINTS_BACK_RIGHT_ANKLE_FOOT);
    }

    public static String getTitle(Context context, int moleId)
    {
        int resId = getTitleResourceId(context, moleId);
        return context.getString(resId);
    }

    public static int getTitleResourceId(Context context, int moleId)
    {
        return context.getResources()
                .getIdentifier("z" + moleId, "string", context.getPackageName());
    }

    public static String getSectionString(Context context, int zoneId)
    {
        if(zoneId >= ID_FRONT_START && zoneId <= ID_FRONT_END)
        {
            return "(" + context.getString(R.string.zone_front) + ")";
        }
        else if(zoneId >= ID_BACK_START && zoneId <= ID_BACK_END)
        {
            return "(" + context.getString(R.string.zone_back) + ")";
        }
        else
        {
            return "";
        }
    }

    public static int[] getStartEndIdLimits(int side)
    {
        if(side == BodyMapView.STATE_BODY_FRONT)
        {
            return new int[] {ID_FRONT_START, ID_FRONT_END};
        }
        else if(side == BodyMapView.STATE_BODY_BACK)
        {
            return new int[] {ID_BACK_START, ID_BACK_END};
        }
        else if(side == BodyMapView.STATE_BODY_HEAD)
        {
            return new int[] {ID_HEAD_START, ID_HEAD_END};
        }
        else
        {
            throw new RuntimeException("Invalid side value " + side);
        }
    }
}


