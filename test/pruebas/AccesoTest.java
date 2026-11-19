package pruebas;

import capaEntidad.Usuario;
import org.junit.Test;
import servlets.Acceso;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pruebas unitarias del control de acceso por roles.
 *
 * Acceso.tieneRol es el metodo que decide que modulos ve cada usuario,
 * asi que un error aqui dejaria a un mesero entrando a Usuarios o a un
 * cocinero sin ver el panel de cocina. Estas pruebas se ejecutan de forma
 * automatica en cada push y en cada Pull Request.
 */
public class AccesoTest {

    private Usuario usuarioConRol(String rol) {
        Usuario u = new Usuario();
        u.setNombre("Prueba");
        u.setRol(rol);
        return u;
    }

    @Test
    public void adminTieneRolAdmin() {
        assertTrue(Acceso.tieneRol(usuarioConRol("ADMIN"), "ADMIN"));
    }

    @Test
    public void meseroNoTieneRolAdmin() {
        assertFalse(Acceso.tieneRol(usuarioConRol("MESERO"), "ADMIN"));
    }

    @Test
    public void meseroEstaEntreVariosRoles() {
        assertTrue(Acceso.tieneRol(usuarioConRol("MESERO"), "ADMIN", "MESERO", "COCINA"));
    }

    @Test
    public void noDistingueMayusculas() {
        assertTrue(Acceso.tieneRol(usuarioConRol("admin"), "ADMIN"));
    }

    @Test
    public void usuarioNuloNoTieneNingunRol() {
        assertFalse(Acceso.tieneRol(null, "ADMIN"));
    }

    @Test
    public void usuarioSinRolNoTieneNingunRol() {
        assertFalse(Acceso.tieneRol(usuarioConRol(null), "ADMIN"));
    }

    @Test
    public void sinRolesIndicadosDevuelveFalso() {
        assertFalse(Acceso.tieneRol(usuarioConRol("ADMIN")));
    }
}
