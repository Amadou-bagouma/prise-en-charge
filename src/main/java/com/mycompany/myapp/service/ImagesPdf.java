package com.mycompany.myapp.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openpdf.text.Image;

/**
 * Transforme une image deposee par un utilisateur en image posable dans un PDF.
 *
 * <p>OpenPDF ne lit que le PNG, le JPEG, le GIF et quelques formats anciens. Une signature
 * deposee en WebP - ce que l'ecran de depot accepte, et ce que produisent volontiers les
 * telephones - lui est illisible : elle disparaissait du document sans autre trace qu'une
 * ligne de journal. Les formats qu'OpenPDF ignore passent donc par ImageIO, qui les decode
 * (le WebP grace au module TwelveMonkeys), avant d'etre remis a OpenPDF.
 */
public final class ImagesPdf {

    private ImagesPdf() {}

    /**
     * L'image prete a poser dans un PDF.
     *
     * @param octets le contenu du fichier depose.
     * @return l'image, ou {@code null} si aucun lecteur ne la reconnait.
     * @throws IOException si le fichier est reconnu mais ne se decode pas.
     */
    public static Image lire(byte[] octets) throws IOException {
        try {
            return Image.getInstance(octets);
        } catch (IOException | RuntimeException formatInconnu) {
            BufferedImage decodee = ImageIO.read(new ByteArrayInputStream(octets));
            if (decodee == null) {
                return null;
            }
            // Couleur nulle : la transparence d'une signature est conservee, elle ne se pose pas
            // sur un rectangle blanc.
            return Image.getInstance(decodee, null);
        }
    }
}
