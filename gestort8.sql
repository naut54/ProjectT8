
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestort8`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias_tbl`
--

CREATE TABLE `categorias_tbl` (
  `idCategoria` int(11) NOT NULL,
  `sNombre` varchar(64) NOT NULL,
  `sDescripcion` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `categorias_tbl`
--

INSERT INTO `categorias_tbl` (`idCategoria`, `sNombre`, `sDescripcion`) VALUES
(1, 'CPU', 'Procesador'),
(2, 'GPU', 'Tarjeta Grafica');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_ventas_tbl`
--

CREATE TABLE `detalle_ventas_tbl` (
  `idDetalle` int(11) NOT NULL,
  `idVenta` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `iCantidad` int(11) DEFAULT NULL,
  `dPrecio` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `detalle_ventas_tbl`
--

INSERT INTO `detalle_ventas_tbl` (`idDetalle`, `idVenta`, `idProducto`, `iCantidad`, `dPrecio`) VALUES
(1, 1, 3, 2, 800.00),
(2, 4, 1, 1, 800.00),
(3, 4, 2, 1, 600.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos_tbl`
--

CREATE TABLE `productos_tbl` (
  `idProducto` int(11) NOT NULL,
  `sNombre` varchar(128) NOT NULL,
  `sDescripcion` text DEFAULT NULL,
  `dPrecio` decimal(10,2) DEFAULT NULL,
  `iCodigoProducto` bigint(20) DEFAULT NULL,
  `idCategoria` int(11) NOT NULL,
  `bActivo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `productos_tbl`
--

INSERT INTO `productos_tbl` (`idProducto`, `sNombre`, `sDescripcion`, `dPrecio`, `iCodigoProducto`, `idCategoria`, `bActivo`) VALUES
(1, 'Test1', 'Descripcion', 800.00, 4001, 1, 1),
(2, 'Test2', 'Descripcion', 600.00, 4002, 2, 1),
(3, 'Test3', 'Desc', 500.60, 4003, 1, 1),
(4, 'Test4', 'Desc', 800.00, 4004, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `stock_tbl`
--

CREATE TABLE `stock_tbl` (
  `idStock` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `iCantidad` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `stock_tbl`
--

INSERT INTO `stock_tbl` (`idStock`, `idProducto`, `iCantidad`) VALUES
(1, 1, 0),
(2, 2, 0),
(3, 3, 0),
(4, 4, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas_tbl`
--

CREATE TABLE `ventas_tbl` (
  `idVenta` int(11) NOT NULL,
  `sFecha` date DEFAULT curdate(),
  `dTotal` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `ventas_tbl`
--

INSERT INTO `ventas_tbl` (`idVenta`, `sFecha`, `dTotal`) VALUES
(1, '2025-02-17', 1500.00),
(2, '2025-02-17', 800.00),
(3, '2025-02-17', 400.00),
(4, '2025-02-18', 1400.00);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categorias_tbl`
--
ALTER TABLE `categorias_tbl`
  ADD PRIMARY KEY (`idCategoria`);

--
-- Indices de la tabla `detalle_ventas_tbl`
--
ALTER TABLE `detalle_ventas_tbl`
  ADD PRIMARY KEY (`idDetalle`);

--
-- Indices de la tabla `imagenes_tbl`
--
ALTER TABLE `imagenes_tbl`
  ADD PRIMARY KEY (`idImagen`);

--
-- Indices de la tabla `productos_tbl`
--
ALTER TABLE `productos_tbl`
  ADD PRIMARY KEY (`idProducto`),
  ADD UNIQUE KEY `iCodigoProducto` (`iCodigoProducto`);

--
-- Indices de la tabla `stock_tbl`
--
ALTER TABLE `stock_tbl`
  ADD PRIMARY KEY (`idStock`);

--
-- Indices de la tabla `ventas_tbl`
--
ALTER TABLE `ventas_tbl`
  ADD PRIMARY KEY (`idVenta`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categorias_tbl`
--
ALTER TABLE `categorias_tbl`
  MODIFY `idCategoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `detalle_ventas_tbl`
--
ALTER TABLE `detalle_ventas_tbl`
  MODIFY `idDetalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `imagenes_tbl`
--
ALTER TABLE `imagenes_tbl`
  MODIFY `idImagen` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `productos_tbl`
--
ALTER TABLE `productos_tbl`
  MODIFY `idProducto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `stock_tbl`
--
ALTER TABLE `stock_tbl`
  MODIFY `idStock` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `ventas_tbl`
--
ALTER TABLE `ventas_tbl`
  MODIFY `idVenta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
